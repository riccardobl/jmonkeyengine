package com.jme3.scene;

import java.io.IOException;
import java.nio.Buffer;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.util.NativeObject;
import com.jme3.util.NativeObjectManager;

/**
 * VertexBufferPointer
 */
public class VertexBufferPointer extends VertexBuffer{
    private VertexBuffer ref;
   
    public VertexBufferPointer ref(VertexBuffer vb){
        ref=vb;
        data=vb.data;
        return this;
    }

    public VertexBuffer ref(){
        return ref;
    }
    VertexBufferPointer(){
    }

    public VertexBufferPointer(Type type,int components,Format format,int instancespan){
        setNumComponents(components);
        setBufferType(type);
        setFormat(format);
        setInstanceSpan(instancespan);
    }

    @Override
    public void setId(int id){
       ref.forceId(id);
    }

    @Override
    public int getId(){
        return ref.getId();
    }
    
    @Override
    public void setUpdateNeeded(){
        ref.setUpdateNeeded();
    }

   

    @Override
    public boolean isUpdateNeeded(){
        return ref.isUpdateNeeded();
    }

   
 
    
 
    
    public void dispose() {
       ref.dispose();
    }

  

    public boolean invariant() {
        return ref.invariant();
    }
 
    public int getOffset() {
        return super.getOffset();
    }

    public void setOffset(int offset) {
        super.setOffset(offset);
    }

   
    public int getStride() {
        return super.getStride();
    }

    
    public void setStride(int stride) {
        super.setStride(stride);
    }

    
    public Buffer getData(){
        return ref.getData();
    }
    
  
    public Buffer getDataReadOnly() {
        return ref.getDataReadOnly();    
   
    }

   
    public Usage getUsage(){
        return ref.getUsage();
    }

   
    public void setUsage(Usage usage){
        throw new UnsupportedOperationException("Can't do that for a VertexBufferPointer");
    }

    public void setNumComponents(int n){
        super.setNumComponents(n);
    }

   
    public void setNormalized(boolean normalized){
        super.setNormalized(normalized);
    }

   
    public boolean isNormalized(){
        return super.isNormalized();
    }

  

    
    public boolean isInstanced() {
        return super.isInstanced();
    }
 
  
    public void setInstanceSpan(int i) {
        super.setInstanceSpan(i);
    }
    
    public int getInstanceSpan() {
        return super.getInstanceSpan();
    }
    
   
    public Type getBufferType(){
        return super.getBufferType();
    }


    public void setBufferType(Type t){
        super.setBufferType(t);
    }

    
    public Format getFormat(){
        return super.getFormat();
    }

    public void setFormat(Format f){
        super.setFormat(f);
    }

    
    public int getNumComponents(){
        return super.getNumComponents();
    }

   
    public int getNumElements(){
        return super.getNumElements();
       
    }

    public int getBaseInstanceCount() {
        return super.getBaseInstanceCount();
    }


    public void setupData(Usage usage, int components, Format format, Buffer data){
        throw new UnsupportedOperationException("Can't do that for a VertexBufferPointer");
    }

  
    public void updateData(Buffer data){
        throw new UnsupportedOperationException("Can't do that for a VertexBufferPointer");
    }

  
    public boolean hasDataSizeChanged() {
        return ref.hasDataSizeChanged();
    }

    @Override
    public void clearUpdateNeeded(){
       ref.clearUpdateNeeded();
    }

    public void convertToHalf(){
        ref.clearUpdateNeeded();
    }

   
    public void compact(int numElements){
        ref.compact(numElements);

    }

 
    public void setElementComponent(int elementIndex, int componentIndex, Object val){
        throw new UnsupportedOperationException("Can't do that for a VertexBufferPointer");
    }

  
    public Object getElementComponent(int elementIndex, int componentIndex){
        throw new UnsupportedOperationException("Can't do that for a VertexBufferPointer");
    }

    
    public void copyElement(int inIndex, VertexBuffer outVb, int outIndex){
        throw new UnsupportedOperationException("Can't do that for a VertexBufferPointer");

    }

    public void copyElements(int inIndex, VertexBuffer outVb, int outIndex, int len){
        throw new UnsupportedOperationException("Can't do that for a VertexBufferPointer");

    }

   
    public static Buffer createBuffer(Format format, int components, int numElements){
        throw new UnsupportedOperationException("Can't do that for a VertexBufferPointer");

    }

   
    @Override
    public VertexBuffer clone(){
        return new VertexBufferPointer().ref(ref);

    }

  
    public VertexBufferPointer clone(Type overrideType){
        VertexBufferPointer c= new VertexBufferPointer().ref(ref);
        c.setBufferType(overrideType);
        return c;
    }

    @Override
    public String toString(){
        return ref.toString();
    }

    @Override
    public void resetObject() {
        ref.resetObject();
    }

    @Override
    public void deleteObject(Object rendererObject) {
        ref.deleteObject(rendererObject);
    }
    
    @Override
    protected void deleteNativeBuffers() {
       ref.deleteNativeBuffers();
    }
            
    @Override
    public NativeObject createDestructableClone(){
        return ref.createDestructableClone();
    }

    @Override
    public long getUniqueId() {
        return ref.getUniqueId();
    }
    
    @Override
    public void write(JmeExporter ex) throws IOException {
        throw new UnsupportedOperationException("Can't do that for a VertexBufferPointer");

      
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        throw new UnsupportedOperationException("Can't do that for a VertexBufferPointer");

    }


}