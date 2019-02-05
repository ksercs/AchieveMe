from django.db import models

class Aims(models.Model):
    User_name = models.CharField(max_length=120)
	#Aim_id, # models.IntegerField()
	#List_id,# = models.IntegerField()
	#цParrentAim_id,# = models.IntegerField()
    Name = models.CharField(max_length=120, default='')
	#Deadline = models.DateTimeField(auto_now_add=True)
	#IsImportant = models.BooleanField(default=0)
	#Remind = models.BooleanField(default=0)
	#TimeToDo = models.IntegerField(default=0)
	
class Setting(models.Model):
	user_name = models.CharField(max_length = 20)
	is_notification_to_email = models.BooleanField(default = True)
	Gmt = models.IntegerField(default = '+3')

	