from PIL import Image
import os

# Ursprungsbild
input_image_path = "./512x512/icon_buchhaltung_512x512.png"

# Allgemeine Größen
general_sizes = [16, 32, 48, 64, 128, 256, 512]

# Android Launcher-Icon Größen
android_sizes = {
    "drawable-mdpi": 48,
    "drawable-hdpi": 72,
    "drawable-xhdpi": 96,
    "drawable-xxhdpi": 144,
    "drawable-xxxhdpi": 192
}

# iOS App-Icon Größen
ios_sizes = {
    "AppIcon-29pt": [29, 58, 87],          # Small Icon
    "AppIcon-40pt": [40, 80, 120],        # Spotlight Icon
    "AppIcon-60pt": [60, 120, 180],       # App Icon
    "AppIcon-76pt": [76, 152, 228],       # iPad Icon
    "AppIcon-83.5pt": [83.5, 167],        # iPad Pro Icon
    "AppIcon-1024pt": [1024]              # App Store Icon
}

def resize_and_save(image_path, sizes, subfolder_prefix=""):
    """
    Erstellt und speichert Icons direkt im aktuellen Verzeichnis mit entsprechender Ordnerstruktur.
    """
    with Image.open(image_path) as img:
        for subfolder, size_data in sizes.items():
            folder_path = os.path.join(os.getcwd(), subfolder_prefix + subfolder)
            os.makedirs(folder_path, exist_ok=True)
            if isinstance(size_data, list):
                for size in size_data:
                    resized_img = img.resize((int(size), int(size)), Image.LANCZOS) 
                    output_file = os.path.join(folder_path, f"icon_buchhaltung_{int(size)}x{int(size)}.png")  
                    resized_img.save(output_file)
                    print(f"Gespeichert: {output_file}")
            else:
                resized_img = img.resize((int(size_data), int(size_data)), Image.LANCZOS) 
                output_file = os.path.join(folder_path, f"icon_buchhaltung_{int(size_data)}x{int(size_data)}.png") 
                resized_img.save(output_file)
                print(f"Gespeichert: {output_file}")

# Generiere allgemeine Größen
resize_and_save(input_image_path, {f"{size}x{size}": size for size in general_sizes})

# Generiere Android Launcher-Icons
resize_and_save(input_image_path, android_sizes, subfolder_prefix="android/")

# Generiere iOS App-Icons
resize_and_save(input_image_path, ios_sizes, subfolder_prefix="ios/")
