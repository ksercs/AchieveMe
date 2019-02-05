# Generated by Django 2.1.5 on 2019-02-05 13:49

from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Aims',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('User_name', models.CharField(max_length=120)),
                ('Name', models.CharField(default='', max_length=120)),
            ],
        ),
        migrations.CreateModel(
            name='Setting',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('user_name', models.CharField(max_length=20)),
                ('is_notification_to_email', models.BooleanField(default=True)),
                ('Gmt', models.IntegerField(default='+3')),
            ],
        ),
    ]
