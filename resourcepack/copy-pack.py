import shutil;
import os;
import time;

if os.path.exists("resource-pack-directory.txt"):
    dir = open("resource-pack-directory.txt").read() + "/LSD"
    if not os.path.isdir(dir):
        os.mkdir(dir)
    else:
        shutil.rmtree(dir)
        os.mkdir(dir)
    
    shutil.copytree("LSD", dir, dirs_exist_ok=True)
    print("Resource pack copied to Minecraft directory successfully!")

else:
    path = input("Paste the path to your Minecraft's 'resourcepacks' folder here and rerun this script. Alternatively, you may paste the path into 'resource-pack-directory.txt' (which is within this directory):")
    f = open("resource-pack-directory.txt", "w")
    f.write(path)

os.system("pause")