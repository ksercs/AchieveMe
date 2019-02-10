from __future__ import print_function
from apiclient import discovery
from httplib2 import Http
from oauth2client import file, client, tools
import datetime
import os
import json

SCOPES = 'https://www.googleapis.com/auth/calendar'
from .models import Aim

try:
	import argparse
	flags = tools.argparser.parse_args([])
except ImportError:
	flags = None

def calendar_authorization(username):
	store = open('app/static/secret_data/' + username +'.json', 'w')
	store.close()
	store = file.Storage('app/static/secret_data/' + username +'.json')
	creds = store.get()
	if not creds or creds.invalid:
		flow = client.flow_from_clientsecrets('app/client_secret.json', SCOPES)
		creds = tools.run_flow(flow, store, flags)


def add_to_calendar(aim, Gmt):
	store = file.Storage('app/static/secret_data/' + aim.user_name +'.json')
	creds = store.get()
	if not creds or creds.invalid:
		flow = client.flow_from_clientsecrets('app/client_secret.json', SCOPES)
		creds = tools.run_flow(flow, store, flags)
	GCAL = discovery.build('calendar', 'v3', http=creds.authorize(Http()))
	start_time = aim.deadline - datetime.timedelta(minutes=1)
	EVENT = {
		'summary':  aim.name + " oсталась неделя до конца цели",
		'start': {'dateTime': str(start_time - datetime.timedelta(days=7)).replace(' ', 'T')},
		'end': {'dateTime': str(aim.deadline - datetime.timedelta(days=7)).replace(' ', 'T')},
	}

	e = GCAL.events().insert(calendarId='primary', sendNotifications=True, body=EVENT).execute()
	EVENT = {
		'summary': aim.name + " остался день до конца цели",
		'start': {'dateTime': str(start_time - datetime.timedelta(days=1)).replace(' ', 'T')},
		'end': {'dateTime': str(aim.deadline - datetime.timedelta(days=1)).replace(' ', 'T')},
	}

	e = GCAL.events().insert(calendarId='primary', sendNotifications=True, body=EVENT).execute()