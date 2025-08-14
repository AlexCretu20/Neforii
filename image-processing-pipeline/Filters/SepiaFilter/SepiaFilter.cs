// using System.Drawing;
// using AppCore;

// namespace Filters.SepiaFilter;

// public class SepiaFilter : IFilter
// {
//     public Bitmap Apply(Bitmap inputImage)
//     {
//         var sepiaImg = new Bitmap(inputImage.Width, inputImage.Height);
//         for (int y = 0; y < inputImage.Height; y++)
//         {
//             for (int x = 0; x < inputImage.Width; x++)
//             {
//                 var pixel = inputImage.GetPixel(x, y);

//                 int tr = (int)(pixel.R * 0.393 + pixel.G * 0.769 + pixel.B * 0.189);
//                 int tg = (int)(pixel.R * 0.349 + pixel.G * 0.686 + pixel.B * 0.168);
//                 int tb = (int)(pixel.R * 0.272 + pixel.G * 0.534 + pixel.B * 0.131);
                
//                 tr = Math.Min(255, tr);
//                 tg = Math.Min(255, tg);
//                 tb = Math.Min(255, tb);

//                 var sepiaColor = Color.FromArgb(tr, tg, tb);
//                 sepiaImg.SetPixel(x, y, sepiaColor);
//             }
//         }
//         return sepiaImg;
//     }
// }




using AppCore;
using SixLabors.ImageSharp;
using SixLabors.ImageSharp.PixelFormats;
using SixLabors.ImageSharp.Processing;

namespace Filters.SepiaFilter
{
    public sealed class SepiaFilter : IFilter
    {
        public string Name => "Sepia";
        public Image<Rgba32> Apply(Image<Rgba32> input) => input.Clone(ctx => ctx.Sepia());
    }
}

