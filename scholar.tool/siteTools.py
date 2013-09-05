import urllib2
import urllib
import re


def getCiteInfo(title, cookie, debug=False):
    url = "http://scholar.google.com/scholar"

    #title = "Efficient approach based on hybrid bounding volume hierarchy for real-time collision detection"
    param = {}
    param['hl'] = 'en'
    param['q'] = title

    url = url + '?' + urllib.urlencode(param)
    if(debug):
        print url
    headers = {'User-Agent' : 'Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1541.0'}

    req = urllib2.Request(url, '', headers)
    response = urllib2.urlopen(req)
    result = response.read()
    #the first <h3 is the right anwser for the search
    #first div class="gs_r"
    firstindex_begin = result.find('<div class=\"gs_r\" style=')
    firstindex_end = result.find('<div class=\"gs_r\" style=', firstindex_begin+1)
    paperinfo = result[firstindex_begin : firstindex_end]
    #print paperinfo
    citeid_begin = paperinfo.find('gs_ocit(event,') 
    citeid_end = paperinfo.find(')', citeid_begin)
    citeid = paperinfo[citeid_begin + len('gs_ocit(event,') + 1 : citeid_end-1]
    if(debug):
        print citeid

    if(len(citeid) == 0):
        print title + "cannot find."
        return title + "cannot find"
    response.close()
    citeurl = "http://scholar.google.com/scholar.bib?"
    params={}
    params['q']='info:'+citeid+':scholar.google.com/'
    params['output']='citation'
    citeurl += urllib.urlencode(params)
    if(debug):
        print citeurl
    headers['Referer'] = url
    headers['Host'] = 'scholar.google.com'
    headers['Cookie'] = cookie
    req = urllib2.Request(citeurl, '', headers)

    try:
        response = urllib2.urlopen(req)
    except urllib2.HTTPError:
        print 'error, request again'
        response = urllib2.urlopen(req)

    result = response.read()
    return result




