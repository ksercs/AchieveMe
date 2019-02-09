import subprocess
import os 
import sys
from PyQt5.QtWidgets import QDesktopWidget,QApplication
app = QApplication(sys.argv)
q= QDesktopWidget().availableGeometry()
print("width =", q.width())
print("height =", q.height())

def fcollage(): 
    tree = os.walk("/Users/mak/AchieveMe/AchieveMe/media/images")
    app = QApplication(sys.argv)
    q= QDesktopWidget().availableGeometry()    
    for i in tree:
        image = i[2]
    pictures = "montage -geometry 300x "  
    q.width()
    big_pictures = "montage -geometry " + str(q.width()) + "x" + str(q.height())  
    for i in image:
        pictures += " /Users/mak/AchieveMe/AchieveMe/media/images/" + i
        big_pictures += " /Users/mak/AchieveMe/AchieveMe/media/images/" + i
    pictures += " /Users/mak/AchieveMe/AchieveMe/media/collage.png"
    big_pictures += " /Users/mak/AchieveMe/AchieveMe/media/big_collage.png"
    print(pictures)
    print(big_pictures)
    subprocess.call(pictures, shell=True)
    subprocess.call(big_pictures, shell=True)