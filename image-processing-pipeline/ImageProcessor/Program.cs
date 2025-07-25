using System;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Drawing;
using AppCore;

namespace ImageProcessor
{
    class Program
    {
        static void Main(string[] args)
        {
            var baseDir = AppContext.BaseDirectory;
            var dlls = Directory.GetFiles(baseDir, "*.dll");

            
            
            var filters = dlls
                .Select(Assembly.LoadFrom)
                .SelectMany(asm => asm.GetTypes())
                .Where(t => typeof(IFilter).IsAssignableFrom(t)
                            && !t.IsInterface
                            && !t.IsAbstract)
                .Select(t => (IFilter)Activator.CreateInstance(t))
                .ToList();

            if (!filters.Any())
            {
                Console.Error.WriteLine("No filters found in the output folder.");
                return;
            }

            var inputDir  = Path.Combine(baseDir, "Images", "Input");
            var outputDir = Path.Combine(baseDir, "Images", "Output");
            Directory.CreateDirectory(outputDir);

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
                    using var outImg = filter.Apply(srcImg);
                    var outName = $"{filter.GetType().Name}_{fileName}";
                    var outPath = Path.Combine(outputDir, outName);
                    outImg.Save(outPath);
                    Console.WriteLine($" {fileName} → {outName}");
                }
            }

            Console.WriteLine("Processing complete.");
        }
    }
}
