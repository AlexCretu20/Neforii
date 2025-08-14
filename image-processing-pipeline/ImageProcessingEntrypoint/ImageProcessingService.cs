using AppCore;
using SixLabors.ImageSharp;
using SixLabors.ImageSharp.PixelFormats;
using SixLabors.ImageSharp.Formats.Png; 

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
                using var result = filter.Apply(src.Clone()); 
                var outName = $"{filter.Name}_{fileName}";
                var outPath = Path.Combine(outputDir, outName);
                await result.SaveAsync(outPath);             
                Console.WriteLine($"{fileName} -> {outName}");
            }
        }
    }

    public static async Task<byte[]> ApplyFilterAsync(string imagePath, int filterId, List<IFilter?> filters)
    {
        if (string.IsNullOrWhiteSpace(imagePath))
            throw new ArgumentException("ImagePath cannot be null or empty.", nameof(imagePath));
        if (!System.IO.File.Exists(imagePath))
            throw new FileNotFoundException("The file does not exist.", imagePath);
        if (filterId < 0 || filterId >= filters.Count)
            throw new ArgumentOutOfRangeException(nameof(filterId), "The filter id is out of range.");

        using var src = await Image.LoadAsync<Rgba32>(imagePath);

        if (filters[filterId] is null)
        {
            using var msOriginal = new MemoryStream();
            await src.SaveAsync(msOriginal, new PngEncoder());
            return msOriginal.ToArray();
        }

        var filtered = filters[filterId]!.Apply(src); // filtrele tale produc o imagine nouă
        try
        {
            using var ms = new MemoryStream();
            await filtered.SaveAsync(ms, new PngEncoder());
            return ms.ToArray();
        }
        finally
        {
            filtered.Dispose();
        }
    }
}

