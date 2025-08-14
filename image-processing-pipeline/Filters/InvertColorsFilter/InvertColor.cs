// using System.Drawing;
// using AppCore;
// namespace InvertColorsFilter;

// public class InvertColor: IFilter
// {
//     public Bitmap Apply(Bitmap inputImage)
//     {
//         var invertedImg = new Bitmap(inputImage.Width, inputImage.Height);

//         for (int y = 0; y < inputImage.Height; y++)
//         {
//             for (int x = 0; x < inputImage.Width; x++)
//             {
//                 var pixel = inputImage.GetPixel(x, y);
//                 var invertedColor = Color.FromArgb(
//                     255 - pixel.R,
//                     255 - pixel.G,
//                     255 - pixel.B
//                 );
//                 invertedImg.SetPixel(x, y, invertedColor);
//             }
//         }

//         return invertedImg;
//     }

// }




using AppCore;
using SixLabors.ImageSharp;
using SixLabors.ImageSharp.PixelFormats;
using SixLabors.ImageSharp.Processing;

namespace InvertColorsFilter
{
    public sealed class InvertColor : IFilter
    {
        public string Name => "Invert";
        public Image<Rgba32> Apply(Image<Rgba32> input) => input.Clone(ctx => ctx.Invert());
    }
}

