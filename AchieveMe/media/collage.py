import subprocess
import os  
def fcollage():
    tree = os.walk("/home/mtimokhin/AchieveMe/AchieveMe/media/images")
    for i in tree:
        image = i[2][1:]
    pictures = "montage -geometry 500x200 "
    for i in image:
        pictures += "/home/mtimokhin/AchieveMe/AchieveMe/media/images/" + i + " "
    pictures += "/home/mtimokhin/AchieveMe/AchieveMe/media/collage.png"
    subprocess.call(pictures, shell=True)
