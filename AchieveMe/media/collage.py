import subprocess
import os  
def fcollage():
    tree = os.walk("~/AchieveMe/AchieveMe/media/images")
    for i in tree:
        image = i[2][1:]
    pictures = "montage "
    for i in image:
        pictures += "~/AchieveMe/AchieveMe/media/images/" + i + " "
    pictures += "~/AchieveMe/AchieveMe/media/collage.png"
    subprocess.call(pictures, shell=True)
