from django.db import models
from django.utils.timezone import now
from django.urls import reverse

class Aim(models.Model):
    user_name  = models.CharField       (max_length = 120)
    list_id         = models.IntegerField     (default = -1)
    parent_id    = models.IntegerField    (default = -1)
    name 		  = models.CharField        (max_length = 120, default ='')
    deadline       = models.DateTimeField(default = now)
    is_important = models.BooleanField  (default = 0)
    is_remind     = models.BooleanField  (default = 0)
    is_completed 	   = models.BooleanField  (default = 0)
    time_to_do   = models.IntegerField    ()
    image = models.ImageField(upload_to='images/', default='images/cat.jpg')
    
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
    
