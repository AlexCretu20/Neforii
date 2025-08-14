// using System.Drawing;
// using AppCore;

// namespace DoNothingFilter;

// public class DoNothingFilter : IFilter
// { 
//     public Bitmap Apply(Bitmap inputImage)
//     {
//         return inputImage;
//     }
// }



using AppCore;
using SixLabors.ImageSharp;
using SixLabors.ImageSharp.PixelFormats;

namespace DoNothingFilter
{
    public sealed class DoNothingFilter : IFilter
    {
        public string Name => "DoNothing";
        public Image<Rgba32> Apply(Image<Rgba32> input) => input.Clone();
    }
}
