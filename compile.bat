cd src
dir *.java /s /b > ../files.txt
cd ../

javac @files.txt -d bin/ -cp lib/*;src/
del files.txt