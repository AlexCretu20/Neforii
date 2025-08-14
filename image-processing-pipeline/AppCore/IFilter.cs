using SixLabors.ImageSharp;
using SixLabors.ImageSharp.PixelFormats;

namespace AppCore
{
    public interface IFilter
    {
        string Name { get; }              
        Image<Rgba32> Apply(Image<Rgba32> input);
    }
}
