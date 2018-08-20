/*
 * Copyright (c) 2009-2012 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
#include "jmePhysicsSpace.h"
#include "jmeBulletUtil.h"

#ifdef BT_THREADSAFE
    #ifdef JLOCK_IS_SPINLOCK
        btSpinMutex java_safecall_mutex;
        #define jlockinit(dw) {}
        #define jlock(dw) if(dw->is_multithread)  java_safecall_mutex.lock()
        #define junlock(dw) if(dw->is_multithread) java_safecall_mutex.unlock()
        #define jlockdestroy(dw) {}
    #elif defined( BT_USE_OPENMP)
        #include <omp.h>
        omp_lock_t java_safecall_mutex;
        #define jlockinit(dw) if(dw->is_multithread) omp_init_lock(&java_safecall_mutex)
        #define jlock(dw) if(dw->is_multithread)  omp_set_lock(&java_safecall_mutex)
        #define junlock(dw) if(dw->is_multithread) omp_unset_lock(&java_safecall_mutex)
        #define jlockdestroy(dw) if(dw->is_multithread) omp_destroy_lock(&java_safecall_mutex)
    #else
        #include <pthread.h>
        pthread_mutex_t java_safecall_mutex;
        #define jlockinit(dw) if(dw->is_multithread) pthread_mutex_init(&java_safecall_mutex, NULL)
        #define jlock(dw) if(dw->is_multithread) pthread_mutex_lock(&java_safecall_mutex)
        #define junlock(dw) if(dw->is_multithread)  pthread_mutex_unlock(&java_safecall_mutex)
        #define jlockdestroy(dw) if(dw->is_multithread)    pthread_mutex_destroy(&java_safecall_mutex)
    #endif
#else
    #define jlockinit(dw) {}
    #define jlock(dw) {}
    #define junlock(dw) {}
    #define jlockdestroy(dw) {}
#endif
/**
 * Author: Normen Hansen
 */
jmePhysicsSpace::jmePhysicsSpace(JNIEnv* env, jobject javaSpace) {
    //TODO: global ref? maybe not -> cleaning, rather callback class?
    this->javaPhysicsSpace = env->NewWeakGlobalRef(javaSpace);
    this->env = env;
    env->GetJavaVM(&vm);
    if (env->ExceptionCheck()) {
        env->Throw(env->ExceptionOccurred());
        return;
    }
}

void jmePhysicsSpace::attachThread() {
#ifdef ANDROID
    vm->AttachCurrentThread((JNIEnv**) &env, NULL);
#elif defined (JNI_VERSION_1_2)
    vm->AttachCurrentThread((void**) &env, NULL);
#else
    vm->AttachCurrentThread(&env, NULL);
#endif
}

JNIEnv* jmePhysicsSpace::getEnv() {
    attachThread();
    return this->env;
}

void jmePhysicsSpace::stepSimulation(jfloat tpf, jint maxSteps, jfloat accuracy) {
    dynamicsWorld->stepSimulation(tpf, maxSteps, accuracy);
}

void jmePhysicsSpace::createPhysicsSpace(jfloat minX, jfloat minY, jfloat minZ, jfloat maxX, jfloat maxY, jfloat maxZ, jint broadphaseId, jint threads) {
    btCollisionConfiguration* collisionConfiguration = new btDefaultCollisionConfiguration();

    btVector3 min = btVector3(minX, minY, minZ);
    btVector3 max = btVector3(maxX, maxY, maxZ);

    btBroadphaseInterface* broadphase;

    switch (broadphaseId) {
        case 0:
            broadphase = new btSimpleBroadphase();
            break;
        case 1:
            broadphase = new btAxisSweep3(min, max);
            break;
        case 2:
            //TODO: 32bit!
            broadphase = new btAxisSweep3(min, max);
            break;
        case 3:
            broadphase = new btDbvtBroadphase();
            break;
    }
    btDiscreteDynamicsWorld* world;
    printf("############ INITIALIZING PHYSICS SPACE ###########\n");

    #ifdef BT_THREADSAFE
    if(threads>1){
      this->is_multithread=true;
      #ifdef BT_USE_OPENMP
        printf("Use Multithread solver with %d openmp threads\n",threads);
        btSetTaskScheduler(btGetOpenMPTaskScheduler());
      #else
        printf("Use Multithread solver with %d posix threads\n",threads);
        btSetTaskScheduler(btCreateDefaultTaskScheduler());
      #endif
      btCollisionDispatcherMt* dispatcher =
          new btCollisionDispatcherMt(collisionConfiguration, 40);
      btGImpactCollisionAlgorithm::registerAlgorithm(dispatcher);
      btConstraintSolverPoolMt* solver_pool =
          new btConstraintSolverPoolMt(threads);
      // create parallel solver
      btConstraintSolver* solver = new btSequentialImpulseConstraintSolverMt();
      world = new btDiscreteDynamicsWorldMt(dispatcher, broadphase, solver_pool,
                                            solver, collisionConfiguration);
    }else{    
        this->is_multithread=false;

    #else
    if(threads>1){
        printf("Warning: Cannot run multithread solver, this build is not threadsafe. A single thread solver will be used insted.\n");
    }   
    #endif
  
  
    printf("Using Single thread solver\n");

    btCollisionDispatcher* dispatcher = new btCollisionDispatcher(collisionConfiguration);
    btGImpactCollisionAlgorithm::registerAlgorithm(dispatcher);
    btConstraintSolver* solver = new btSequentialImpulseConstraintSolver();
    //create dynamics world
    world = new btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
    
    #ifdef BT_THREADSAFE
    }
    #endif

    dynamicsWorld = world;
    jlockinit(this);

    dynamicsWorld->setWorldUserInfo(this);

    broadphase->getOverlappingPairCache()->setInternalGhostPairCallback(new btGhostPairCallback());
    dynamicsWorld->setGravity(btVector3(0, -9.81f, 0));

    struct jmeFilterCallback : public btOverlapFilterCallback {
        // return true when pairs need collision

        virtual bool needBroadphaseCollision(btBroadphaseProxy* proxy0, btBroadphaseProxy * proxy1) const {
            //            bool collides = (proxy0->m_collisionFilterGroup & proxy1->m_collisionFilterMask) != 0;
            //            collides = collides && (proxy1->m_collisionFilterGroup & proxy0->m_collisionFilterMask);
            bool collides = (proxy0->m_collisionFilterGroup & proxy1->m_collisionFilterMask) != 0;
            collides = collides && (proxy1->m_collisionFilterGroup & proxy0->m_collisionFilterMask);
            if (collides) {
                btCollisionObject* co0 = (btCollisionObject*) proxy0->m_clientObject;
                btCollisionObject* co1 = (btCollisionObject*) proxy1->m_clientObject;
                jmeUserPointer *up0 = (jmeUserPointer*) co0 -> getUserPointer();
                jmeUserPointer *up1 = (jmeUserPointer*) co1 -> getUserPointer();
                if (up0 != NULL && up1 != NULL) {
                    collides = (up0->group & up1->groups) != 0 || (up1->group & up0->groups) != 0;

                    if(collides){
                        jmePhysicsSpace *dynamicsWorld = (jmePhysicsSpace *)up0->space;
                        JNIEnv* env = dynamicsWorld->getEnv();
                        jobject javaPhysicsSpace = env->NewLocalRef(dynamicsWorld->getJavaPhysicsSpace());
                        jobject javaCollisionObject0 = env->NewLocalRef(up0->javaCollisionObject);
                        jobject javaCollisionObject1 = env->NewLocalRef(up1->javaCollisionObject);

                        jboolean notifyResult = env->CallBooleanMethod(javaPhysicsSpace, jmeClasses::PhysicsSpace_notifyCollisionGroupListeners, javaCollisionObject0, javaCollisionObject1);

                        env->DeleteLocalRef(javaPhysicsSpace);
                        env->DeleteLocalRef(javaCollisionObject0);
                        env->DeleteLocalRef(javaCollisionObject1);

                        if (env->ExceptionCheck()) {
                            env->Throw(env->ExceptionOccurred());
                            return collides;
                        }

                        collides = (bool) notifyResult;
                    }

                    //add some additional logic here that modified 'collides'
                    return collides;
                }
                return false;
            }
            return collides;
        }
    };
    dynamicsWorld->getPairCache()->setOverlapFilterCallback(new jmeFilterCallback());
    dynamicsWorld->setInternalTickCallback(&jmePhysicsSpace::preTickCallback, static_cast<void *> (this), true);
    dynamicsWorld->setInternalTickCallback(&jmePhysicsSpace::postTickCallback, static_cast<void *> (this));
    if (gContactProcessedCallback == NULL) {
        gContactProcessedCallback = &jmePhysicsSpace::contactProcessedCallback;
    }
}

void jmePhysicsSpace::preTickCallback(btDynamicsWorld *world, btScalar timeStep) {
    jmePhysicsSpace* dynamicsWorld = (jmePhysicsSpace*) world->getWorldUserInfo();
    // jlock(dynamicsWorld);
    JNIEnv* env = dynamicsWorld->getEnv();
    jobject javaPhysicsSpace = env->NewLocalRef(dynamicsWorld->getJavaPhysicsSpace());
    if (javaPhysicsSpace != NULL) {
        env->CallVoidMethod(javaPhysicsSpace, jmeClasses::PhysicsSpace_preTick, timeStep);
        env->DeleteLocalRef(javaPhysicsSpace);
        if (env->ExceptionCheck()) {
            env->Throw(env->ExceptionOccurred());
            // junlock(dynamicsWorld);
            return;
        }
    }
    // junlock(dynamicsWorld);
}

void jmePhysicsSpace::postTickCallback(btDynamicsWorld *world, btScalar timeStep) {
    jmePhysicsSpace* dynamicsWorld = (jmePhysicsSpace*) world->getWorldUserInfo();
    // jlock(dynamicsWorld);
    JNIEnv* env = dynamicsWorld->getEnv();
    jobject javaPhysicsSpace = env->NewLocalRef(dynamicsWorld->getJavaPhysicsSpace());
    if (javaPhysicsSpace != NULL) {
        env->CallVoidMethod(javaPhysicsSpace, jmeClasses::PhysicsSpace_postTick, timeStep);
        env->DeleteLocalRef(javaPhysicsSpace);
        if (env->ExceptionCheck()) {
            env->Throw(env->ExceptionOccurred());
            // junlock(dynamicsWorld);
            return;
        }
    }
    // junlock(dynamicsWorld);
}

bool jmePhysicsSpace::contactProcessedCallback(btManifoldPoint &cp, void *body0, void *body1) {
    //    printf("contactProcessedCallback %d %dn", body0, body1);
    btCollisionObject* co0 = (btCollisionObject*)body0;
    jmeUserPointer *up0 = (jmeUserPointer*) co0 -> getUserPointer();
    btCollisionObject* co1 = (btCollisionObject*) body1;
    jmeUserPointer *up1 = (jmeUserPointer*) co1 -> getUserPointer();
    if (up0 != NULL) {
        jmePhysicsSpace *dynamicsWorld = (jmePhysicsSpace *)up0->space;
        jlock(dynamicsWorld);

        if (dynamicsWorld != NULL) {
            JNIEnv* env = dynamicsWorld->getEnv();
            jobject javaPhysicsSpace = env->NewLocalRef(dynamicsWorld->getJavaPhysicsSpace());
            if (javaPhysicsSpace != NULL) {
                jobject javaCollisionObject0 = env->NewLocalRef(up0->javaCollisionObject);
                jobject javaCollisionObject1 = env->NewLocalRef(up1->javaCollisionObject);
                env->CallVoidMethod(javaPhysicsSpace, jmeClasses::PhysicsSpace_addCollisionEvent, javaCollisionObject0, javaCollisionObject1, (jlong) & cp);
                env->DeleteLocalRef(javaPhysicsSpace);
                env->DeleteLocalRef(javaCollisionObject0);
                env->DeleteLocalRef(javaCollisionObject1);
                if (env->ExceptionCheck()) {
                    env->Throw(env->ExceptionOccurred());
                    junlock(dynamicsWorld);
                    return true;
                }
            }
        }
        junlock(dynamicsWorld);
    }
    return true;
}

btDynamicsWorld* jmePhysicsSpace::getDynamicsWorld() {
    return dynamicsWorld;
}

jobject jmePhysicsSpace::getJavaPhysicsSpace() {
    return javaPhysicsSpace;
}

jmePhysicsSpace::~jmePhysicsSpace() {
    jmePhysicsSpace *jmeDynamicsWorld = (jmePhysicsSpace *)dynamicsWorld;
    jlockdestroy(jmeDynamicsWorld);
    delete(dynamicsWorld);
}
