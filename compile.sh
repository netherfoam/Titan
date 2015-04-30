find src/ -name '*.java' > files.txt
javac @files.txt -cp "lib/*" -d bin
rm files.txt
