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

