using System;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Drawing;
using System.Security.AccessControl;
using AppCore;

namespace ImageProcessingEntrypoint
{
    class Program
    {
        private static void Main(string[] args)
        {
            Console.WriteLine(PathConfig.RootDir);

            var dlls = Directory.GetFiles(PathConfig.FiltersDir, "*.dll"); // all dlls
            
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
            var imageFiles = Directory.EnumerateFiles(PathConfig.InputDir)
                .Where(f => PathConfig.ImageExtensions.Any(ext =>
                    f.EndsWith(ext, StringComparison.OrdinalIgnoreCase)))
                .ToList();

            if (!imageFiles.Any())
            {
                Console.Error.WriteLine($"No images found in {PathConfig.InputDir}");
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
                    var outPath = Path.Combine(PathConfig.OutputDir, outName);
                    outImg.Save(outPath);
                    Console.WriteLine($" {fileName} -> {outName}");
                }
            }

            Console.WriteLine("Processing complete.");
        }
    }
}
