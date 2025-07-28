using System;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Drawing;
using System.Security.AccessControl;
using AppCore;

namespace ImageProcessor
{
    class Program
    {
        private static void Main(string[] args)
        {
            var baseDir = AppContext.BaseDirectory; // bin/debug/net9,0
            var rootDir = Path.GetFullPath(Path.Combine(baseDir, "..", "..", "..")); 
            var filtersDir = Path.GetFullPath(Path.Combine(rootDir, "..", "Filters", "FiltersDlls", "net9.0")); 

            var dlls = Directory.GetFiles(filtersDir, "*.dll"); // all dlls
            
            var filters = dlls
                .Select(Assembly.LoadFrom)  
                .SelectMany(asm => asm.GetTypes()) // ia tot din dll
                .Where(t => typeof(IFilter).IsAssignableFrom(t) // implementeaza interfata dar nu sunt interfete sau clase abstracte
                            && !t.IsInterface
                            && !t.IsAbstract)
                .Select(t => (IFilter)Activator.CreateInstance(t))
                .ToList();

            if (!filters.Any())
            {
                Console.Error.WriteLine("No filters found.");
                return;
            }
            
            var inputDir  = Path.Combine(rootDir, "Images", "Input");
            var outputDir = Path.Combine(rootDir, "Images", "Output");

            var imageFiles = Directory.EnumerateFiles(inputDir)
                .Where(f => f.EndsWith(".jpg", StringComparison.OrdinalIgnoreCase)
                         || f.EndsWith(".png", StringComparison.OrdinalIgnoreCase))
                .ToList();

            if (!imageFiles.Any())
            {
                Console.Error.WriteLine($"No images found in {inputDir}");
                return;
            }

            foreach (var imgPath in imageFiles)
            {
                using var srcImg = (Bitmap)Image.FromFile(imgPath);
                var fileName = Path.GetFileName(imgPath);
                

                foreach (var filter in filters)
                {
                    using var clonedImg = new Bitmap(srcImg); // e dubios fara clona, aparent SystemDrawing.Bitmap nu prea e recomandat
                    using var outImg = filter.Apply(clonedImg);
                    
                    var outName = $"{filter.GetType().Name}_{fileName}";
                    var outPath = Path.Combine(outputDir, outName);
                    outImg.Save(outPath);
                    Console.WriteLine($" {fileName} -> {outName}");
                }
            }

            Console.WriteLine("Processing complete.");
        }
    }
}
