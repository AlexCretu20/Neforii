// using System.Drawing;
// using System.Drawing.Imaging;
// using System.Runtime.InteropServices;
// using AppCore;

// namespace Filters.BlurFilter;

// public class BlurFilter : IFilter
// {
//     public int Radius { get; }
//     public BlurFilter() : this(10) {}
//     public BlurFilter(int radius = 3) => Radius = Math.Max(1, radius);

//     public Bitmap Apply(Bitmap input)
//     {
//         var src = new Bitmap(input.Width, input.Height, PixelFormat.Format32bppArgb);
//         src.SetResolution(input.HorizontalResolution, input.VerticalResolution);
//         using (var g = Graphics.FromImage(src)) g.DrawImageUnscaled(input, 0, 0);

//         var tmp = new Bitmap(src.Width, src.Height, PixelFormat.Format32bppArgb);
//         var dst = new Bitmap(src.Width, src.Height, PixelFormat.Format32bppArgb);
//         dst.SetResolution(input.HorizontalResolution, input.VerticalResolution);

//         var bdSrc = src.LockBits(new Rectangle(0,0,src.Width,src.Height), ImageLockMode.ReadOnly, src.PixelFormat);
//         var bdTmp = tmp.LockBits(new Rectangle(0,0,tmp.Width,tmp.Height), ImageLockMode.WriteOnly, tmp.PixelFormat);
//         var bdDst = dst.LockBits(new Rectangle(0,0,dst.Width,dst.Height), ImageLockMode.WriteOnly, dst.PixelFormat);

//         try
//         {
//             int w = src.Width, h = src.Height, stride = bdSrc.Stride, px = 4;
//             int dia = Radius * 2 + 1;

//             byte[] s = new byte[stride * h];
//             byte[] t = new byte[stride * h];
//             byte[] d = new byte[stride * h];

//             Marshal.Copy(bdSrc.Scan0, s, 0, s.Length);

//             for (int y = 0; y < h; y++)
//             {
//                 int row = y * stride;
//                 int sumA = 0, sumR = 0, sumG = 0, sumB = 0;

//                 for (int k = -Radius; k <= Radius; k++)
//                 {
//                     int cx = Math.Clamp(k, 0, w - 1) * px + row;
//                     sumB += s[cx + 0]; sumG += s[cx + 1]; sumR += s[cx + 2]; sumA += s[cx + 3];
//                 }

//                 for (int x = 0; x < w; x++)
//                 {
//                     int idx = row + x * px;
//                     t[idx + 0] = (byte)(sumB / dia);
//                     t[idx + 1] = (byte)(sumG / dia);
//                     t[idx + 2] = (byte)(sumR / dia);
//                     t[idx + 3] = (byte)(sumA / dia);

//                     int xOut = Math.Clamp(x - Radius, 0, w - 1) * px + row;
//                     int xIn  = Math.Clamp(x + Radius + 1, 0, w - 1) * px + row;
//                     sumB += s[xIn + 0] - s[xOut + 0];
//                     sumG += s[xIn + 1] - s[xOut + 1];
//                     sumR += s[xIn + 2] - s[xOut + 2];
//                     sumA += s[xIn + 3] - s[xOut + 3];
//                 }
//             }

//             for (int x = 0; x < w; x++)
//             {
//                 int sumA = 0, sumR = 0, sumG = 0, sumB = 0;

//                 for (int k = -Radius; k <= Radius; k++)
//                 {
//                     int cy = Math.Clamp(k, 0, h - 1);
//                     int idx = cy * stride + x * px;
//                     sumB += t[idx + 0]; sumG += t[idx + 1]; sumR += t[idx + 2]; sumA += t[idx + 3];
//                 }

//                 for (int y = 0; y < h; y++)
//                 {
//                     int idx = y * stride + x * px;
//                     d[idx + 0] = (byte)(sumB / dia);
//                     d[idx + 1] = (byte)(sumG / dia);
//                     d[idx + 2] = (byte)(sumR / dia);
//                     d[idx + 3] = (byte)(sumA / dia);

//                     int yOut = Math.Clamp(y - Radius, 0, h - 1) * stride + x * px;
//                     int yIn  = Math.Clamp(y + Radius + 1, 0, h - 1) * stride + x * px;
//                     sumB += t[yIn + 0] - t[yOut + 0];
//                     sumG += t[yIn + 1] - t[yOut + 1];
//                     sumR += t[yIn + 2] - t[yOut + 2];
//                     sumA += t[yIn + 3] - t[yOut + 3];
//                 }
//             }

//             Marshal.Copy(d, 0, bdDst.Scan0, d.Length);
//         }
//         finally
//         {
//             src.UnlockBits(bdSrc);
//             tmp.UnlockBits(bdTmp);
//             dst.UnlockBits(bdDst);
//             tmp.Dispose();
//             src.Dispose();
//         }

//         return dst;
//     }
// }




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


