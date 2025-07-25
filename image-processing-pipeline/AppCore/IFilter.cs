using System.Drawing;

namespace AppCore
{ 
    public interface IFilter
    {
        Bitmap Apply(Bitmap inputImage);
    }
}