package us.ihmc.euclid.referenceFrame;

import org.junit.Test;
import us.ihmc.euclid.referenceFrame.exceptions.ReferenceFrameMismatchException;
import us.ihmc.euclid.referenceFrame.interfaces.FrameQuaternionReadOnly;
import us.ihmc.euclid.referenceFrame.interfaces.FrameTuple2DReadOnly;
import us.ihmc.euclid.referenceFrame.interfaces.FrameTuple3DReadOnly;
import us.ihmc.euclid.referenceFrame.tools.EuclidFrameAPITestTools;
import us.ihmc.euclid.referenceFrame.tools.EuclidFrameRandomTools;
import us.ihmc.euclid.tools.EuclidCoreRandomTools;
import us.ihmc.euclid.tuple2D.interfaces.Tuple2DBasics;
import us.ihmc.euclid.tuple2D.interfaces.Tuple2DReadOnly;
import us.ihmc.euclid.tuple3D.interfaces.Tuple3DBasics;
import us.ihmc.euclid.tuple4D.Tuple4DReadOnlyTest;
import us.ihmc.euclid.tuple4D.interfaces.QuaternionBasics;
import us.ihmc.euclid.tuple4D.interfaces.QuaternionReadOnly;

import java.util.Random;

import static org.junit.Assert.fail;

public abstract class FrameQuaternionReadOnlyTest<T extends FrameQuaternion> extends Tuple4DReadOnlyTest<T>
{
   Random random = new Random(System.currentTimeMillis());

   @Override
   public final T createEmptyTuple()
   {
      return createTuple(ReferenceFrame.getWorldFrame(), 0.0, 0.0, 0.0, 1.0);
   }

   public final T createEmptyTuple(ReferenceFrame referenceFrame)
   {
      return createTuple(referenceFrame, 0.0, 0.0, 0.0, 1.0);
   }

   @Override
   public final T createRandomTuple(Random random)
   {
      return createTuple(ReferenceFrame.getWorldFrame(), random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble());
   }

   @Override
   public final T createTuple(double x, double y, double z, double s)
   {
      return createTuple(ReferenceFrame.getWorldFrame(), x, y, z, s);
   }

   public abstract T createTuple(ReferenceFrame referenceFrame, double x, double y, double z, double s);

   @Override
   public double getEpsilon()
   {
      return 1e-10;
   }

   @Test
   public void testOverloading() throws Exception
   {
      EuclidFrameAPITestTools.assertOverloadingWithFrameObjects(FrameQuaternionReadOnly.class, QuaternionReadOnly.class, true);
   }

   @Test
   public void testReferenceFrameChecks()
   {
      ReferenceFrame referenceFrame;
      FrameQuaternionReadOnly quaternion;

      ReferenceFrame otherFrame;

      FrameQuaternionReadOnly fqro;
      FrameQuaternion fq;
      QuaternionBasics qb;
      FrameTuple2DReadOnly ft2dro;
      Tuple2DBasics t2db;
      Tuple2DReadOnly t2dro;
      FrameTuple2D ft2d;
      FrameTuple3D ft3d0;
      FrameTuple3D ft3d1;
      FrameTuple3DReadOnly ft3dro;
      Tuple3DBasics t3db;
      FrameVector3D fv3d;

      // transform
      for (int i = 0; i < 100; ++i)
      {
         referenceFrame = EuclidFrameRandomTools.generateRandomReferenceFrame(random);
         quaternion = EuclidFrameRandomTools.generateRandomFrameQuaternion(random, referenceFrame);

         if (random.nextDouble() > 0.5)
         {
            ft2dro = EuclidFrameRandomTools.generateRandomFramePoint2D(random, referenceFrame);
            t2db = EuclidCoreRandomTools.generateRandomPoint2D(random);
            t2dro = EuclidCoreRandomTools.generateRandomPoint2D(random);
            ft2d = EuclidFrameRandomTools.generateRandomFramePoint2D(random, referenceFrame);
            ft3d0 = EuclidFrameRandomTools.generateRandomFramePoint3D(random, referenceFrame);
            ft3d1 = EuclidFrameRandomTools.generateRandomFramePoint3D(random, referenceFrame);
            ft3dro = EuclidFrameRandomTools.generateRandomFramePoint3D(random, referenceFrame);
            t3db = EuclidCoreRandomTools.generateRandomPoint3D(random);
            fqro = EuclidFrameRandomTools.generateRandomFrameQuaternion(random, referenceFrame);
            qb = EuclidCoreRandomTools.generateRandomQuaternion(random);
            fq = EuclidFrameRandomTools.generateRandomFrameQuaternion(random, referenceFrame);

            try
            {
               quaternion.transform(ft2dro, t2db, false);
            }
            catch (ReferenceFrameMismatchException excepted)
            {
               fail();
            }

            try
            {
               quaternion.transform(t2dro, ft2d, false);
            }
            catch (ReferenceFrameMismatchException excepted)
            {
               fail();
            }

            try
            {
               quaternion.transform(ft2dro, ft2d, false);
            }
            catch (ReferenceFrameMismatchException excepted)
            {
               fail();
            }

            try
            {
               quaternion.transform(ft3d0);
            }
            catch (ReferenceFrameMismatchException excepted)
            {
               fail();
            }

            try
            {
               quaternion.transform(ft3d0, ft3d1);
            }
            catch (ReferenceFrameMismatchException excepted)
            {
               fail();
            }

            try
            {
               quaternion.transform(ft3dro, t3db);
            }
            catch (ReferenceFrameMismatchException excepted)
            {
               fail();
            }

            try
            {
               quaternion.transform(fqro, qb);
            }
            catch (ReferenceFrameMismatchException excepted)
            {
               fail();
            }

            try
            {
               quaternion.transform(fqro, fq);
            }
            catch (ReferenceFrameMismatchException excepted)
            {
               fail();
            }

            try
            {
               quaternion.transform(fq);
            }
            catch (ReferenceFrameMismatchException excepted)
            {
               fail();
            }
         }
         else
         {
            otherFrame = EuclidFrameRandomTools.generateRandomReferenceFrame(random);

            ft2dro = EuclidFrameRandomTools.generateRandomFramePoint2D(random, otherFrame);
            t2db = EuclidCoreRandomTools.generateRandomPoint2D(random);
            t2dro = EuclidCoreRandomTools.generateRandomPoint2D(random);
            ft2d = EuclidFrameRandomTools.generateRandomFramePoint2D(random, otherFrame);
            ft3d0 = EuclidFrameRandomTools.generateRandomFramePoint3D(random, otherFrame);
            ft3d1 = EuclidFrameRandomTools.generateRandomFramePoint3D(random, otherFrame);
            ft3dro = EuclidFrameRandomTools.generateRandomFramePoint3D(random, otherFrame);
            t3db = EuclidCoreRandomTools.generateRandomPoint3D(random);
            fqro = EuclidFrameRandomTools.generateRandomFrameQuaternion(random, otherFrame);
            qb = EuclidCoreRandomTools.generateRandomQuaternion(random);
            fq = EuclidFrameRandomTools.generateRandomFrameQuaternion(random, otherFrame);

            try
            {
               quaternion.transform(ft2dro, t2db, false);
               fail();
            }
            catch (ReferenceFrameMismatchException excepted)
            {

            }

            try
            {
               quaternion.transform(t2dro, ft2d, false);
               fail();
            }
            catch (ReferenceFrameMismatchException excepted)
            {

            }

            try
            {
               quaternion.transform(ft2dro, ft2d, false);
               fail();
            }
            catch (ReferenceFrameMismatchException excepted)
            {

            }

            try
            {
               quaternion.transform(ft3d0);
               fail();
            }
            catch (ReferenceFrameMismatchException excepted)
            {

            }

            try
            {
               quaternion.transform(ft3d0, ft3d1);
               fail();
            }
            catch (ReferenceFrameMismatchException excepted)
            {

            }

            try
            {
               quaternion.transform(ft3dro, t3db);
               fail();
            }
            catch (ReferenceFrameMismatchException excepted)
            {

            }

            try
            {
               quaternion.transform(fqro, qb);
               fail();
            }
            catch (ReferenceFrameMismatchException excepted)
            {

            }

            try
            {
               quaternion.transform(fqro, fq);
               fail();
            }
            catch (ReferenceFrameMismatchException excepted)
            {

            }

            try
            {
               quaternion.transform(fq);
               fail();
            }
            catch (ReferenceFrameMismatchException excepted)
            {

            }
         }
      }

      // get
      for (int i = 0; i < 100; ++i) {
         referenceFrame = EuclidFrameRandomTools.generateRandomReferenceFrame(random);
         quaternion = EuclidFrameRandomTools.generateRandomFrameQuaternion(random, referenceFrame);

         if (random.nextDouble() > 0.5) {
            fv3d = EuclidFrameRandomTools.generateRandomFrameVector3D(random, referenceFrame);

            try {
               quaternion.get(fv3d);
            } catch (ReferenceFrameMismatchException excepted) {
               fail();
            }
         } else {
            fv3d = EuclidFrameRandomTools.generateRandomFrameVector3D(random, EuclidFrameRandomTools.generateRandomReferenceFrame(random));

            try {
               quaternion.get(fv3d);
               fail();
            } catch (ReferenceFrameMismatchException ignored) {

            }
         }
      }

      // getEuler
      for (int i = 0; i < 100; ++i) {
         referenceFrame = EuclidFrameRandomTools.generateRandomReferenceFrame(random);
         quaternion = EuclidFrameRandomTools.generateRandomFrameQuaternion(random, referenceFrame);

         if (random.nextDouble() > 0.5) {
            fv3d = EuclidFrameRandomTools.generateRandomFrameVector3D(random, referenceFrame);

            try {
               quaternion.getEuler(fv3d);
            } catch (ReferenceFrameMismatchException excepted) {
               fail();
            }
         } else {
            fv3d = EuclidFrameRandomTools.generateRandomFrameVector3D(random, EuclidFrameRandomTools.generateRandomReferenceFrame(random));

            try {
               quaternion.getEuler(fv3d);
               fail();
            } catch (ReferenceFrameMismatchException ignored) {

            }
         }
      }

      // distance
      for (int i = 0; i < 100; ++i) {
         referenceFrame = EuclidFrameRandomTools.generateRandomReferenceFrame(random);
         quaternion = EuclidFrameRandomTools.generateRandomFrameQuaternion(random, referenceFrame);

         if (random.nextDouble() > 0.5) {
            fqro = EuclidFrameRandomTools.generateRandomFrameQuaternion(random, referenceFrame);

            try {
               quaternion.distance(fqro);
            } catch (ReferenceFrameMismatchException excepted) {
               fail();
            }
         } else {
            fqro = EuclidFrameRandomTools.generateRandomFrameQuaternion(random, EuclidFrameRandomTools.generateRandomReferenceFrame(random));

            try {
               quaternion.distance(fqro);
               fail();
            } catch (ReferenceFrameMismatchException ignored) {

            }
         }
      }
   }
}
