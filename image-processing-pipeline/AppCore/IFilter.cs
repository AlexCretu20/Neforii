using System.Drawing;

namespace AppCore
{ 
    public interface IFilter // daca adaug un filtru doar il creez si dau build doar la dll-ul de filtru, pentru ca filtrele sunt dynamic linked cu imageprocessor
    {
        Bitmap Apply(Bitmap inputImage); // Bitmap are Width, Heightm, GetPixel, SetPixel, Save
    }
}