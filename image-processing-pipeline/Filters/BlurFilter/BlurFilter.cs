using AppCore;
using SixLabors.ImageSharp;
using SixLabors.ImageSharp.PixelFormats;
using SixLabors.ImageSharp.Processing;

namespace Filters.BlurFilter
{
    public sealed class BlurFilter : IFilter
    {
        public int Radius { get; }
        public string Name => $"Blur{Radius}";
        public BlurFilter() : this(10) {}
        public BlurFilter(int radius) => Radius = Math.Max(1, radius);
        public Image<Rgba32> Apply(Image<Rgba32> input) => input.Clone(ctx => ctx.GaussianBlur((float)Radius));
    }
}


