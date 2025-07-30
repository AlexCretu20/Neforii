using System.Drawing;
using AppCore;

namespace ImageProcessingEntrypoint;

public class ImageProcessingService
{
    public static void ProcessImages(List<IFilter> filters, string inputDir, string outputDir, string[] imageExtensions)
    {
        var imageFiles = Directory.EnumerateFiles(PathConfig.InputDir)
            .Where(f => PathConfig.ImageExtensions.Any(ext =>
                f.EndsWith(ext, StringComparison.OrdinalIgnoreCase)))
            .ToList();
        if (!imageFiles.Any())
        {
            Console.Error.WriteLine("No images found in the input directory.");
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
    }
}