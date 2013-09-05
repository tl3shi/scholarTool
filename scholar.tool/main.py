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

if len(sys.argv) < 3:
    print 'u can use: main.py inputfile.txt outputfile.bib'
    print 'default config used, paper_list.txt as input,papers-bib-in-google.bib as output'
    print 'parsing......'
else:
    default = False

if(default):
    paper_list_file_name = 'paper_list.txt'
else:
    paper_list_file_name = sys.argv[1] 
    
paper_list_file = open(paper_list_file_name, 'r')
for line in paper_list_file.readlines():
    papernames.append(line)
paper_list_file.close()


paper_bib_infos = []
for title in papernames:
    bib_info = getCiteInfo(title, cookie, debug)
    paper_bib_infos.append(bib_info)

if(default):
    paper_bib_file_name = 'papers-bib-in-google.bib'
else:
    paper_bib_file_name = sys.argv[2] 

paper_bib_file = open(paper_bib_file_name, 'w')
paper_bib_file.writelines("\n\n".join(paper_bib_infos))
paper_bib_file.close()

print 'done.\n'
