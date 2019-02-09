import subprocess
import os 
import sys
from AchieveMe.settings import MEDIA_ROOT

def fcollage(): 
    tree = os.walk(MEDIA_ROOT)  
    for i in tree:
        image = i[2]
    pictures = "montage -geometry 300x "  
    big_pictures = "montage -geometry 300x " 
    for i in image:
        pictures += MEDIA_ROOT + 'images/' + i + " "
        big_pictures += MEDIA_ROOT + 'images/' + i + " "
    pictures += MEDIA_ROOT + 'collage.png'
    big_pictures += MEDIA_ROOT + "big_collage.png"
    print(pictures)
    print(big_pictures)
    subprocess.call(pictures, shell=True)
    subprocess.call(big_pictures, shell=True)
