git remote add upstream git@github.com:jMonkeyEngine/jmonkeyengine.git

git fetch upstream

git checkout -b master
git checkout master
git reset --hard upstream/master
git push origin master --force


git checkout -b PBRisComing
git checkout PBRisComing
git reset --hard upstream/PBRisComing
git push origin PBRisComing --force

git checkout -b experimental
git checkout experimental
git reset --hard upstream/experimental
git push origin experimental --force

git checkout -b 3.0
git checkout 3.0
git reset --hard upstream/3.0
git push origin 3.0 --force

git checkout frk
