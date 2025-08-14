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

