# coding=utf8
from siteTools import *
import os, sys
papernames=[]
default = True
debug = False 
cookie_path = '_cookie'
if not os.path.exists(cookie_path):
    print 'u have to set the cookie yourself, in file "_cookie"'
    exit(0)
cookie = open(cookie_path,'r').readlines()[0]

if(cookie == ''):
    print 'u have to set the cookie yourself'
    exit(0)

if len(sys.argv) < 2:
    print 'u can use: single.py keyword'
    print 'parsing......'
else:
    default = False

keyword = sys.argv[1] 
    

bib_info = getCiteInfo(keyword, cookie, debug)

print bib_info

