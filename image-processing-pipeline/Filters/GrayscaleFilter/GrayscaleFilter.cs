using AppCore;
using SixLabors.ImageSharp;
using SixLabors.ImageSharp.PixelFormats;
using SixLabors.ImageSharp.Processing;

namespace Filters.GrayscaleFilter
{
    public sealed class GrayscaleFilter : IFilter
    {
        public string Name => "Grayscale";
        public Image<Rgba32> Apply(Image<Rgba32> input) => input.Clone(ctx => ctx.Grayscale());
    }
}

