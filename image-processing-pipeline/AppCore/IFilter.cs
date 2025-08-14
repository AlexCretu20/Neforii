// using System.Drawing;

// namespace AppCore
// { 
//     public interface IFilter // daca adaug un filtru doar il creez si dau build doar la dll-ul de filtru, pentru ca filtrele sunt dynamic linked cu imageprocessor
//     {
//         Bitmap Apply(Bitmap inputImage); // Bitmap are Width, Heightm, GetPixel, SetPixel, Save
//     }
// }



using SixLabors.ImageSharp;
using SixLabors.ImageSharp.PixelFormats;

namespace AppCore
{
    // Fiecare filtru primește o imagine și întoarce O NOUĂ imagine procesată.
    public interface IFilter
    {
        string Name { get; }                // folosit la numele fișierului de ieșire
        Image<Rgba32> Apply(Image<Rgba32> input);
    }
}
