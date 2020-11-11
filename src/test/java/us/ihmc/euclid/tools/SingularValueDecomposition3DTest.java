package us.ihmc.euclid.tools;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.RandomMatrices_DDRM;
import org.ejml.dense.row.SingularOps_DDRM;
import org.ejml.dense.row.decomposition.svd.SvdImplicitQrDecompose_DDRM;
import org.junit.jupiter.api.Test;

import javafx.util.Pair;
import us.ihmc.euclid.matrix.Matrix3D;
import us.ihmc.euclid.matrix.interfaces.Matrix3DBasics;
import us.ihmc.euclid.matrix.interfaces.Matrix3DReadOnly;
import us.ihmc.euclid.rotationConversion.RotationMatrixConversion;
import us.ihmc.euclid.tuple3D.Vector3D;
import us.ihmc.euclid.tuple4D.Vector4D;

public class SingularValueDecomposition3DTest
{
   private static final int ITERATIONS = 10000;
   private static final double EPSILON = 1.0e-10;

   @Test
   public void test()
   {
      Random random = new Random(36456);
      SingularValueDecomposition3D svd3d = new SingularValueDecomposition3D();

      long ejmlTotalTime = 0;
      long euclidTotalTime = 0;

      List<Supplier<Pair<Matrix3D, String>>> generators = new ArrayList<>();
      generators.add(() -> new Pair<>(new Matrix3D(RandomMatrices_DDRM.symmetric(3, -100.0, 100.0, random)), "Symmetric matrix"));
      generators.add(() -> new Pair<>(new Matrix3D(EuclidCoreRandomTools.nextRotationMatrix(random, Math.PI)), "Rotation matrix"));
      generators.add(() -> new Pair<>(EuclidCoreRandomTools.nextDiagonalMatrix3D(random, 100.0), "Diagonal matrix"));
      generators.add(() -> new Pair<>(EuclidCoreRandomTools.nextMatrix3D(random, 10.0), "General matrix"));
      generators.add(() -> new Pair<>(EuclidCoreRandomTools.nextMatrix3D(random, 10000.0), "Large values matrix"));

      for (int i = 0; i < 5 * ITERATIONS; i++)
      { // warmup
         new SingularValueDecomposition3D().compute(EuclidCoreRandomTools.nextMatrix3D(random));
         ejmlSVDDecomposition(EuclidCoreRandomTools.nextMatrix3D(random), new Matrix3D(), new Matrix3D(), new Matrix3D());
      }

      for (Supplier<Pair<Matrix3D, String>> generator : generators)
      {
         for (int i = 0; i < ITERATIONS; i++)
         {
            Matrix3D A = generator.get().getKey();
            long start = System.nanoTime();
            svd3d.compute(A);
            long end = System.nanoTime();
            euclidTotalTime += end - start;
            double varEpsilon = Math.max(1.0, Math.abs(A.determinant())) * EPSILON;

            Matrix3D Ueuclid = svd3d.getUmat();
            Matrix3D Weuclid = svd3d.getWmat();
            Matrix3D Veuclid = svd3d.getVmat();

            assertTrue(Ueuclid.isRotationMatrix(EPSILON));
            assertTrue(Veuclid.isRotationMatrix(EPSILON));

            Matrix3D A_output = new Matrix3D();
            A_output.set(Ueuclid);
            A_output.multiply(Weuclid);
            A_output.multiplyTransposeOther(Veuclid);

            Matrix3D Wejml = new Matrix3D();
            Matrix3D Uejml = new Matrix3D();
            Matrix3D Vejml = new Matrix3D();
            ejmlTotalTime += ejmlSVDDecomposition(A, Uejml, Wejml, Vejml);

            double[] singularValuesEJML = {Wejml.getM00(), Wejml.getM11(), Wejml.getM22()};
            double[] singularValuesEuclid = {Weuclid.getM00(), Weuclid.getM11(), Math.abs(Weuclid.getM22())};

            if (Weuclid.getM22() < 0.0)
            {
               if (columnDot(2, Uejml, Ueuclid) < 0.0)
                  negateColumn(2, Uejml);
               else
                  negateColumn(2, Vejml);
            }

            for (int col = 0; col < 3; col++)
            {
               if (columnDot(col, Uejml, Ueuclid) < 0.0 && columnDot(col, Vejml, Veuclid) < 0.0)
               {
                  negateColumn(col, Uejml);
                  negateColumn(col, Vejml);
               }
            }

            String messagePrefix = "Iteration: " + i + ", generator: " + generator.get().getValue();
            try
            {

               asertMatrix3DEquals(messagePrefix, A, A_output, varEpsilon);
               assertArrayEquals(singularValuesEJML, singularValuesEuclid, varEpsilon, messagePrefix);
               if (!EuclidCoreTools.epsilonEquals(singularValuesEJML[0], singularValuesEJML[1], EPSILON)
                     && !EuclidCoreTools.epsilonEquals(singularValuesEJML[0], singularValuesEJML[2], EPSILON))
               { // Can't really compare when singular values are equal, since that pretty much implies an infinite number of solutions.
                  asertMatrix3DEquals(messagePrefix, Uejml, Ueuclid, varEpsilon);
                  asertMatrix3DEquals(messagePrefix, Vejml, Veuclid, varEpsilon);
               }
            }
            catch (Throwable e)
            {
               System.out.println(messagePrefix);
               System.out.println("A:\n" + A);
               System.out.println("U EJML:\n" + Uejml);
               System.out.println("U Euclid:\n" + Ueuclid);

               System.out.println("W EJML:\n" + Wejml);
               System.out.println("W Euclid:\n" + Weuclid);

               System.out.println("V EJML:\n" + Vejml);
               System.out.println("V Euclid:\n" + Veuclid);

               throw e;
            }
         }
      }

      double euclidAverageMilllis = euclidTotalTime / 1.0e6 / ITERATIONS / generators.size();
      double ejmlAverageMilllis = ejmlTotalTime / 1.0e6 / ITERATIONS / generators.size();
      System.out.println(String.format("Average time in millisec:\n\t-EJML:%s\n\t-Euclid:%s",
                                       Double.toString(ejmlAverageMilllis),
                                       Double.toString(euclidAverageMilllis)));
   }

   private static void asertMatrix3DEquals(String messagePrefix, Matrix3DReadOnly expected, Matrix3DReadOnly actual, double epsilon)
   {
      double maxError = 0.0;

      for (int row = 0; row < 3; row++)
      {
         for (int col = 0; col < 3; col++)
         {
            maxError = Math.max(maxError, Math.abs(expected.getElement(row, col) - actual.getElement(row, col)));
         }
      }

      EuclidCoreTestTools.assertMatrix3DEquals(EuclidCoreTestTools.addPrefixToMessage(messagePrefix, "max error: " + maxError), expected, actual, epsilon);
   }

   private static double columnDot(int col, Matrix3DReadOnly a, Matrix3DReadOnly b)
   {
      double dot = 0.0;
      for (int row = 0; row < 3; row++)
      {
         dot += a.getElement(row, col) * b.getElement(row, col);
      }
      return dot;
   }

   private static void negateColumn(int col, Matrix3DBasics m)
   {
      for (int row = 0; row < 3; row++)
      {
         m.setElement(row, col, -m.getElement(row, col));
      }
   }

   private static long ejmlSVDDecomposition(Matrix3DReadOnly A, Matrix3DBasics U, Matrix3DBasics W, Matrix3DBasics V)
   {
      DMatrixRMaj A_ejml = new DMatrixRMaj(3, 3);
      A.get(A_ejml);
      SvdImplicitQrDecompose_DDRM svdEJML = new SvdImplicitQrDecompose_DDRM(false, true, true, false);
      long start = System.nanoTime();
      svdEJML.decompose(A_ejml);
      long end = System.nanoTime();
      DMatrixRMaj U_ejml = svdEJML.getU(null, false);
      DMatrixRMaj W_ejml = svdEJML.getW(null);
      DMatrixRMaj V_ejml = svdEJML.getV(null, false);
      SingularOps_DDRM.descendingOrder(U_ejml, false, W_ejml, V_ejml, false);

      U.set(U_ejml);
      W.set(W_ejml);
      V.set(V_ejml);
      return end - start;
   }

   @Test
   public void testSwapColumn()
   {
      Matrix3D original = new Matrix3D(0, 1, 2, 3, 4, 5, 6, 7, 8);

      Matrix3D actual = new Matrix3D();
      Matrix3D expected = new Matrix3D();

      Vector3D tuple1 = new Vector3D();
      Vector3D tuple2 = new Vector3D();

      for (int c1 = 0; c1 < 2; c1++)
      {
         for (int c2 = c1 + 1; c2 < 3; c2++)
         {
            for (boolean negateC1 : new boolean[] {false, true})
            {
               expected.set(original);
               expected.getColumn(c1, tuple1);
               expected.getColumn(c2, tuple2);
               if (negateC1)
                  tuple1.negate();
               expected.setColumn(c1, tuple2);
               expected.setColumn(c2, tuple1);

               actual.set(original);
               SingularValueDecomposition3D.swapColumns(c1, negateC1, c2, actual);
               assertEquals(expected, actual);
            }
         }
      }
   }

   @Test
   public void testSwapElements()
   {
      Random random = new Random(4677);

      for (int i = 0; i < ITERATIONS; i++)
      {
         int c1, c2;

         switch (random.nextInt(3))
         {
            case 0:
               c1 = 0;
               c2 = 1;
               break;
            case 1:
               c1 = 0;
               c2 = 2;
               break;
            default:
               c1 = 1;
               c2 = 2;
               break;
         }

         Vector4D quaternion = EuclidCoreRandomTools.nextVector4D(random);
         quaternion.normalize();
         Matrix3D original = new Matrix3D();
         RotationMatrixConversion.convertQuaternionToMatrix(quaternion.getX(), quaternion.getY(), quaternion.getZ(), quaternion.getS(), original);

         Matrix3D expected = new Matrix3D();
         RotationMatrixConversion.convertQuaternionToMatrix(quaternion.getX(), quaternion.getY(), quaternion.getZ(), quaternion.getS(), expected);
         SingularValueDecomposition3D.swapColumns(c1, true, c2, expected);

         Matrix3D actual = new Matrix3D();
         SingularValueDecomposition3D.swapElements(c1, c2, quaternion);
         RotationMatrixConversion.convertQuaternionToMatrix(quaternion.getX(), quaternion.getY(), quaternion.getZ(), quaternion.getS(), actual);
         EuclidCoreTestTools.assertMatrix3DEquals("Iteration: " + i + ", original:\n" + original + "\nc1=" + c1 + ", c2=" + c2, expected, actual, EPSILON);
      }
   }
}
