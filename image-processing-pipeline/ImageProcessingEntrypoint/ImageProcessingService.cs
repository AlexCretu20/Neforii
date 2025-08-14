// using System.Drawing;
// using AppCore;

// namespace ImageProcessingEntrypoint;

// public class ImageProcessingService
// {
//     public static void ProcessImages(List<IFilter> filters, string inputDir, string outputDir, string[] imageExtensions)
//     {
//         var imageFiles = Directory.EnumerateFiles(PathConfig.InputDir)
//             .Where(f => PathConfig.ImageExtensions.Any(ext =>
//                 f.EndsWith(ext, StringComparison.OrdinalIgnoreCase)))
//             .ToList();
//         if (!imageFiles.Any())
//         {
//             Console.Error.WriteLine("No images found in the input directory.");
//             return;
//         }
        
//         foreach (var imgPath in imageFiles)
//         {
//             using var srcImg = (Bitmap)Image.FromFile(imgPath);
//             var fileName = Path.GetFileName(imgPath);
                

//             foreach (var filter in filters)
//             {
//                 using var clonedImg = new Bitmap(srcImg); // e dubios fara clona, aparent SystemDrawing.Bitmap nu prea e recomandat
//                 using var outImg = filter.Apply(clonedImg);
                    
//                 var outName = $"{filter.GetType().Name}_{fileName}";
//                 var outPath = Path.Combine(PathConfig.OutputDir, outName);
//                 outImg.Save(outPath);
//                 Console.WriteLine($" {fileName} -> {outName}");
//             }
//         }
//     }
// }



using AppCore;
using SixLabors.ImageSharp;
using SixLabors.ImageSharp.PixelFormats;

namespace ImageProcessingEntrypoint;

public class ImageProcessingService
{
    public static async Task ProcessImagesAsync(
        List<IFilter> filters,
        string inputDir,
        string outputDir,
        string[] imageExtensions)
    {
        if (!Directory.Exists(inputDir)) Directory.CreateDirectory(inputDir);
        if (!Directory.Exists(outputDir)) Directory.CreateDirectory(outputDir);

        var imageFiles = Directory.EnumerateFiles(inputDir)
            .Where(f => imageExtensions.Any(ext =>
                f.EndsWith(ext, StringComparison.OrdinalIgnoreCase)))
            .ToList();

        if (imageFiles.Count == 0)
        {
            Console.Error.WriteLine("No images found in the input directory.");
            return;
        }

        foreach (var imgPath in imageFiles)
        {
            using var src = await Image.LoadAsync<Rgba32>(imgPath);
            var fileName = Path.GetFileName(imgPath);

            foreach (var filter in filters)
            {
                using var result = filter.Apply(src); 
                var outName = $"{filter.Name}_{fileName}";
                var outPath = Path.Combine(outputDir, outName);
                await result.SaveAsync(outPath);     
                Console.WriteLine($"{fileName} -> {outName}");
            }
        }
    }
}
