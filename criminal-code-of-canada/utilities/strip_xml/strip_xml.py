import xml.etree.ElementTree as ET
import re


# Set input and output file names
inputFile = 'C-46.xml'	 
outputFile = 'c46.xml'

tree = ET.parse(inputFile) 
root = tree.getroot()

xmlStringIn = ET.tostring(root).decode('utf-8')

# flip and flop to pass string back and forth
flop = xmlStringIn

print("Stripping " + inputFile + "...")

REG_EX = r'<XRef.*?>'
pattern = re.compile(REG_EX)
flip = pattern.sub('', flop)

REG_EX = r'</XRef.*?>'
pattern = re.compile(REG_EX)
flop = pattern.sub('', flip)

REG_EX = r'<DefinedTerm.*?>'
pattern = re.compile(REG_EX)
flip = pattern.sub('', flop)

REG_EX = r'</DefinedTerm.*?>'
pattern = re.compile(REG_EX)
flop = pattern.sub('', flip)

REG_EX = r'<Emph.*?>'
pattern = re.compile(REG_EX)
flip = pattern.sub('', flop)

REG_EX = r'</Emph.*?>'
pattern = re.compile(REG_EX)
flop = pattern.sub('', flip)

REG_EX = r'<Langu.*?>'
pattern = re.compile(REG_EX)
flip = pattern.sub('', flop)

REG_EX = r'</Langu.*?>'
pattern = re.compile(REG_EX)
flop = pattern.sub('', flip)

REG_EX = r'<Repea.*?>'
pattern = re.compile(REG_EX)
flip = pattern.sub('', flop)

REG_EX = r'</Repea.*?>'
pattern = re.compile(REG_EX)
flop = pattern.sub('', flip)

REG_EX = r'<DefinitionR.*?>'
pattern = re.compile(REG_EX)
flip = pattern.sub('', flop)

REG_EX = r'</DefinitionR.*?>'
pattern = re.compile(REG_EX)
flop = pattern.sub('', flip)

REG_EX = r'<ReadAsT.*?>'
pattern = re.compile(REG_EX)
flip = pattern.sub('', flop)

REG_EX = r'</ReadAsT.*?>'
pattern = re.compile(REG_EX)
flop = pattern.sub('', flip)

REG_EX = r'<SectionP.*?>'
pattern = re.compile(REG_EX)
flip = pattern.sub('', flop)

REG_EX = r'</SectionP.*?>'
pattern = re.compile(REG_EX)
flop = pattern.sub('', flip)

REG_EX = r'<Leader.*?>'
pattern = re.compile(REG_EX)
flip = pattern.sub('...........', flop) # note that this makes it odd (end on flip)




print("Strip Complete!")

text_file = open(outputFile, "w")
text_file.write(flip)
text_file.close()

print("Saved to: " + outputFile)
