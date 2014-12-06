import urllib2
import json
import sys

# compare two JSON object
def ordered(obj):
	if isinstance(obj, dict):
		return {k: ordered(v) for k, v in obj.items()}
	if isinstance(obj, list):
		return sorted(ordered(x) for x in obj)
	else:
		return obj

def print_result(test, succ):
	print "Total Test      : ", test
	print "Successful Test : ", succ
	return

api_server = "http://54.149.110.182:8080/restjson/object"
obj1_text = '{"Name":"Tom", "Age":"18", "Major":"Computer Science"}'
obj2_text = '{"Name":"Tom", "Age":"18", "Major":"Computer Science"}' # duplicate object
obj3_text = '{"Animal":"Cat", "Color":"brown", "Friend":"Dog"}'

# url1
# url2
# url3

# total number of testing
num_test = 6
# number of successful testing
num_succ = 0

######## Test 1 Begin: Delete all remained objects ########

# Initialization: delete all objects
response = urllib2.urlopen(api_server)
response_text = response.read()
response_json = json.loads(response_text)

for item in response_json:
	url = item['url']
	request = urllib2.Request(url)
	request.get_method = lambda:'DELETE'
	urllib2.urlopen(request)

response = urllib2.urlopen(api_server)
response_text = response.read()
response_json = json.loads(response_text)

num_item = 0
for item in response_json:
	num_item = num_item+1;

if num_item == 0:
	num_succ = num_succ+1
else:
	print "Test 1 failed: Cannot delete objects."
	print_result(num_test, num_succ)
	sys.exit(1)
######## Test 1 End ########


######## Test 2 Begin: POST a new object ########

data_text = obj1_text

response = urllib2.urlopen(api_server, data_text)
response_text = response.read()

obj_cmp1_text = data_text
obj_cmp1_json = json.loads(obj_cmp1_text);

obj_cmp2_text = response_text
obj_cmp2_json = json.loads(obj_cmp2_text);

obj_cmp1_json['uid'] = obj_cmp2_json['uid']

# used for test 5
uid1 = obj_cmp2_json['uid'];

# created object is the same as the expected object
if ordered(obj_cmp1_json) == ordered(obj_cmp2_json):
	num_succ = num_succ+1
else:
	print "Test 2 failed: Created object is not the expected one."
	print_result(num_test, num_succ)
	sys.exit(1)
######## Test 2 End ########


######## Test 3 Begin: GET a specifit object ########

uid = obj_cmp2_json['uid']
url = api_server+"/"+uid

# used for next test
url1 = url

response = urllib2.urlopen(url)
response_text = response.read()
response_json = json.loads(response_text)

if ordered(obj_cmp1_json) == ordered(response_json):
	num_succ = num_succ+1
else:
	print "Test 3 failed: Cannot GET specific object."
	print_result(num_test, num_succ)
	sys.exit(1)
######## Test 3 End ########


######## Test 4 Begin: Get all objects' url ########

# POST object with obj2_text
response = urllib2.urlopen(api_server, obj2_text)
response_text = response.read()
response_json = json.loads(response_text);
url2 = api_server+"/"+response_json['uid']

# POST object with obj2_text
response = urllib2.urlopen(api_server, obj3_text)
response_text = response.read()
response_json = json.loads(response_text);
url3 = api_server+"/"+response_json['uid']

#print url1
#print url2
#print url3

expected_urls = '[{"url":"'+url1+'"}, '+'{"url":"'+url2+'"}, '+'{"url":"'+url3+'"}]' 

response = urllib2.urlopen(api_server)
response_text = response.read()
tested_urls_json = json.loads(response_text)

expected_urls_json = json.loads(expected_urls);

#print expected_urls_json
#print tested_urls_json

if ordered(expected_urls_json) == ordered(tested_urls_json):
	num_succ = num_succ+1
else:
	print "Test 4 failed: Cannot GET all objects' url."
	print_result(num_test, num_succ)
	sys.exit(1)

######## Test 4 End ########


######## Test 5 Begin: PUT a specific object ########

obj1_new_text = '{'+'"uid":"'+uid1+'", "Name":"Tom", "Age":"18", "School":"Stony Brook", "Grade":"A"}'

request = urllib2.Request(url, obj1_new_text)
request.get_method = lambda:'PUT'
response = urllib2.urlopen(request)

response_text = response.read()
response_json = json.loads(response_text);

obj1_new_json = json.loads(obj1_new_text);

if ordered(response_json) == ordered(obj1_new_json):
	num_succ = num_succ+1
else:
	print "Test 5 failed: Cannot PUT a specific object."
	print_result(num_test, num_succ)
	sys.exit(1)

######## Test 5 End ########


######## Test 6 Begin: DELETE a specific object ########
request = urllib2.Request(url1)
request.get_method = lambda:'DELETE'
urllib2.urlopen(request)

response = urllib2.urlopen(api_server)
response_text = response.read()
response_json = json.loads(response_text)

total_obj = 0
for item in response_json:
	total_obj = total_obj+1

if total_obj == 2:
	num_succ = num_succ+1
else:
	print "Test 6 failed: Cannot DELETE a specific object."
	print_result(num_test, num_succ)
	sys.exit(1)

######## Test 6 End ########

print "All tests are successful!"
print "Total Test      : ", num_test
print "Successful Test : ", num_succ
