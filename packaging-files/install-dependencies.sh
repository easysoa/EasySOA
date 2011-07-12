# Install Node.js
sudo apt-get install libssl-dev
wget http://nodejs.org/dist/node-v0.4.7.tar.gz
tar xf node-v0.4.7.tar.gz
rm node-v0.4.7.tar.gz
cd node-v0.4.7
./configure
make
sudo make install
cd ..
rm -r node-v0.4.7

# Install Sun Java 6 JDK
sudo add-apt-repository "deb http://us.archive.ubuntu.com/ubuntu dapper main restricted"
sudo dpkg --configure -a
sudo apt-get update
sudo apt-get install sun-java6-jdk
export JAVA_HOME=/usr/lib/jvm/java-6-sun/
