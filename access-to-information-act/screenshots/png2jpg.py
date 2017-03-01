import Image
import os

im = Image.open('phone1.jpg')
im.save('phone2.png')
im.close

for filename in os.listdir(directory):
    if filename.endswith(".png"): 
	
        print(os.path.join(directory, filename))
		
        print("here's one!")
		
        continue
    else:
        continue