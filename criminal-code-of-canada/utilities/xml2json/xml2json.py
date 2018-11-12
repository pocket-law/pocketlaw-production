import xml.etree.ElementTree as ET
import xmltodict
import re
try:
    import simplejson as json
except ImportError:
    import json


# Set input and output file names
inputFile = 'C-46.xml'	 
outputFile = 'c46.json'

tree = ET.parse(inputFile) 
root = tree.getroot()

xmlStringIn = ET.tostring(root).decode('utf-8')

print("Converting " + inputFile + "to json\n useing xmltodict module...")

print(json.dumps(xmltodict.parse(xmlStringIn)))

print("Convert Complete! Enjoy your json :)")


text_file = open(outputFile, "w")
text_file.write(json.dumps(xmltodict.parse(xmlStringIn)))
text_file.close()

print("Saved to: " + outputFile)
