import subprocess
import os 

def fcollage(): 
    tree = os.walk("/Users/mak/AchieveMe/AchieveMe/media/images")
    for i in tree:
        image = i[2]
    pictures = "montage -geometry 300x "  
    for i in image:
        pictures += "/Users/mak/AchieveMe/AchieveMe/media/images/" + i
    pictures += " /Users/mak/AchieveMe/AchieveMe/media/collage.png"
    print(pictures)
    subprocess.call(pictures, shell=True)