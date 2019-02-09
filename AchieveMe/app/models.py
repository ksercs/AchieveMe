from django.db import models
from django.utils.timezone import now
from django.urls import reverse
from PIL import Image
from io import BytesIO
from django.core.files.uploadedfile import InMemoryUploadedFile
import sys


class Aim(models.Model):
    user_name  = models.CharField       (max_length = 120)
    list_id         = models.IntegerField     (default = -1)
    parent_id    = models.IntegerField    (default = -1)
    name 		  = models.CharField        (max_length = 120, default ='')
    deadline       = models.DateTimeField(default = now)
    is_important = models.BooleanField  (default = 0)
    is_remind     = models.BooleanField  (default = 0)
    is_completed 	   = models.BooleanField  (default = 0)
    image = models.ImageField(upload_to='images/', default='images/cat.jpg')

    def save(self, *args, **kwargs):
        if self.image:
            im = Image.open(self.image)
            output = BytesIO()
            #im = im.resize((128, 128))
            im.save(output, "JPEG", quality=20)
            output.seek(0)
            self.image = InMemoryUploadedFile(output,'ImageField', "%s.jpg" %self.image.name.split('.')[0], 'images/', sys.getsizeof(output), None)
        super(Aim, self).save(*args, **kwargs)




    def get_absolute_url(self):
        if self.parent_id != -1:
            return reverse('subaim', args=[self.user_name, str(self.list_id), str(self.parent_id)])
        else :
            return reverse('aims', args=[self.user_name, str(self.list_id)])
	
class List(models.Model):
	name 	     = models.CharField(max_length = 120)
	user_name = models.CharField(max_length = 120)
	
class Setting(models.Model):
	user_name 				   = models.CharField     (max_length = 20)
	is_notification_to_email = models.BooleanField(default = True)
	Gmt						       = models.IntegerField  (default = '+3')
	google_sync = models.BooleanField(default = False)

class Description(models.Model):
    aim_id = models.IntegerField()
    text     = models.CharField   (max_length = 500, default = "")
    
class Comment(models.Model):
    aim_id = models.IntegerField()
    text     = models.CharField   (max_length = 500, default = "")
    
class File(models.Model):
    description_id = models.IntegerField(default = 0)
    comment_id = models.IntegerField  (default = 0)
    name = models.FileField(upload_to='images/', default = "")

