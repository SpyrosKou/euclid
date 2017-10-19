package us.ihmc.euclid.referenceFrame;

import us.ihmc.euclid.referenceFrame.exceptions.ReferenceFrameMismatchException;
import us.ihmc.euclid.referenceFrame.interfaces.FrameQuaternionReadOnly;
import us.ihmc.euclid.referenceFrame.interfaces.FrameVector3DReadOnly;
import us.ihmc.euclid.rotationConversion.QuaternionConversion;
import us.ihmc.euclid.tools.QuaternionTools;
import us.ihmc.euclid.tuple3D.interfaces.Vector3DReadOnly;
import us.ihmc.euclid.tuple4D.Quaternion;
import us.ihmc.euclid.tuple4D.interfaces.QuaternionBasics;
import us.ihmc.euclid.tuple4D.interfaces.QuaternionReadOnly;
import us.ihmc.euclid.tuple4D.interfaces.Tuple4DReadOnly;

/**
 * {@code FrameQuaternion} is a quaternion expressed in a given reference frame.
 * <p>
 * In addition to representing a {@link QuaternionBasics}, a {@link ReferenceFrame} is associated to a
 * {@code FrameQuaternion}. This allows, for instance, to enforce, at runtime, that operations on
 * vectors occur in the same coordinate system. Also, via the method
 * {@link #changeFrame(ReferenceFrame)}, one can easily calculates the value of a vector in
 * different reference frame.
 * </p>
 * <p>
 * Because a {@code FrameQuaternion} extends {@code QuaternionBasics}, it is compatible with methods
 * only requiring {@code QuaternionBasics}. However, these methods do NOT assert that the operation
 * occur in the proper coordinate system. Use this feature carefully and always prefer using methods
 * requiring {@code FrameQuaternion}.
 * </p>
 */
public class FrameQuaternion extends FrameTuple4D<FrameQuaternion, Quaternion> implements FrameQuaternionReadOnly, QuaternionBasics
{
   /**
    * Creates a new frame vector and initializes it components to zero and its reference frame to
    * {@link ReferenceFrame#getWorldFrame()}.
    */
   public FrameQuaternion()
   {
      this(ReferenceFrame.getWorldFrame());
   }

   /**
    * Creates a new frame vector and initializes it components to zero and its reference frame to
    * the {@code referenceFrame}.
    *
    * @param referenceFrame the initial frame for this frame vector.
    */
   public FrameQuaternion(ReferenceFrame referenceFrame)
   {
      super(referenceFrame, new Quaternion());
   }

   /**
    * Creates a new frame vector and initializes it with the given components and the given
    * reference frame.
    *
    * @param referenceFrame the initial frame for this frame vector.
    * @param x the x-component.
    * @param y the y-component.
    * @param z the z-component.
    */
   public FrameQuaternion(ReferenceFrame referenceFrame, double x, double y, double z, double s)
   {
      super(referenceFrame, new Quaternion(x, y, z, s));
   }

   /**
    * Creates a new frame quaternion and initializes its component {@code x}, {@code y}, {@code z} in
    * order from the given array and initializes its reference frame.
    *
    * @param referenceFrame the initial frame for this frame vector.
    * @param vectorArray the array containing this quaternion's components. Not modified.
    */
   public FrameQuaternion(ReferenceFrame referenceFrame, double[] vectorArray)
   {
      super(referenceFrame, new Quaternion(vectorArray));
   }

   /**
    * Creates a new frame quaternion and initializes it to {@code tuple4DReadOnly} and to the given
    * reference frame.
    *
    * @param referenceFrame the initial frame for this frame vector.
    * @param tuple4DReadOnly the tuple to copy the components from. Not modified.
    */
   public FrameQuaternion(ReferenceFrame referenceFrame, Tuple4DReadOnly tuple4DReadOnly)
   {
      super(referenceFrame, new Quaternion(tuple4DReadOnly));
   }

   /**
    * Creates a new frame quaternion and initializes it to {@code other}.
    *
    * @param other the tuple to copy the components and reference frame from. Not modified.
    */
   public FrameQuaternion(FrameQuaternionReadOnly other)
   {
      super(other.getReferenceFrame(), new Quaternion(other));
   }

   /**
    * Sets this frame quaternion to {@code other} and then calls {@link #normalize()}.
    *
    * @param other the other frame quaternion to copy the values from. Not modified.
    * @throws ReferenceFrameMismatchException if {@code other} is not expressed in the same
    *            reference frame as {@code this}.
    */
   public final void setAndNormalize(FrameQuaternionReadOnly other)
   {
      checkReferenceFrameMatch(other);
      tuple.setAndNormalize(other);
   }

   /**
    * Sets this frame quaternion to {@code other} and then calls {@link #negate()}.
    *
    * @param other the other frame quaternion to set to. Not modified.
    * @throws ReferenceFrameMismatchException if {@code other} is not expressed in the same
    *            reference frame as {@code this}.
    */
   public final void setAndNegate(FrameQuaternionReadOnly other)
   {
      checkReferenceFrameMatch(other);
      tuple.setAndNegate(other);
   }

   /**
    * Performs a linear interpolation in SO(3) from {@code this} to {@code qf} given the percentage
    * {@code alpha}.
    * <p>
    * The interpolation method used here is often called a <i>Spherical Linear Interpolation</i> or
    * SLERP.
    * </p>
    *
    * @param qf the other quaternion used for the interpolation. Not modified.
    * @param alpha the percentage used for the interpolation. A value of 0 will result in not
    *           modifying this quaternion, while a value of 1 is equivalent to setting this
    *           quaternion to {@code qf}.
    * @throws ReferenceFrameMismatchException if {@code qf} is not expressed in the same
    *            reference frame as {@code this}.
    */
   public final void interpolate(FrameQuaternionReadOnly qf, double alpha)
   {
      checkReferenceFrameMatch(qf);

      double cosHalfTheta = dot(qf);
      double sign = 1.0;

      if (cosHalfTheta < 0.0)
      {
         sign = -1.0;
         cosHalfTheta = -cosHalfTheta;
      }

      double alpha0 = 1.0 - alpha;
      double alphaf = alpha;

      if (1.0 - cosHalfTheta > 1.0e-12)
      {
         double halfTheta = Math.acos(cosHalfTheta);
         double sinHalfTheta = Math.sin(halfTheta);
         alpha0 = Math.sin(alpha0 * halfTheta) / sinHalfTheta;
         alphaf = Math.sin(alphaf * halfTheta) / sinHalfTheta;
      }

      double qx = alpha0 * getX() + sign * alphaf * qf.getX();
      double qy = alpha0 * getY() + sign * alphaf * qf.getY();
      double qz = alpha0 * getZ() + sign * alphaf * qf.getZ();
      double qs = alpha0 * getS() + sign * alphaf * qf.getS();
      set(qx, qy, qz, qs);
   }

   /**
    * Performs a linear interpolation in SO(3) from {@code q0} to {@code qf} given the percentage
    * {@code alpha}.
    * <p>
    * The interpolation method used here is often called a <i>Spherical Linear Interpolation</i> or
    * SLERP.
    * </p>
    *
    * @param q0 the first quaternion used in the interpolation. Not modified.
    * @param qf the second quaternion used in the interpolation. Not modified.
    * @param alpha the percentage to use for the interpolation. A value of 0 will result in setting
    *           this quaternion to {@code q0}, while a value of 1 is equivalent to setting this
    *           quaternion to {@code qf}.
    * @throws ReferenceFrameMismatchException if {@code qf} is not expressed in the same
    *            reference frame as {@code this}.
    */
   public final void interpolate(FrameQuaternionReadOnly q0, QuaternionReadOnly qf, double alpha)
   {
      double cosHalfTheta = q0.dot(qf);
      double sign = 1.0;

      if (cosHalfTheta < 0.0)
      {
         sign = -1.0;
         cosHalfTheta = -cosHalfTheta;
      }

      double alpha0 = 1.0 - alpha;
      double alphaf = alpha;

      if (1.0 - cosHalfTheta > 1.0e-12)
      {
         double halfTheta = Math.acos(cosHalfTheta);
         double sinHalfTheta = Math.sin(halfTheta);
         alpha0 = Math.sin(alpha0 * halfTheta) / sinHalfTheta;
         alphaf = Math.sin(alphaf * halfTheta) / sinHalfTheta;
      }

      double qx = alpha0 * q0.getX() + sign * alphaf * qf.getX();
      double qy = alpha0 * q0.getY() + sign * alphaf * qf.getY();
      double qz = alpha0 * q0.getZ() + sign * alphaf * qf.getZ();
      double qs = alpha0 * q0.getS() + sign * alphaf * qf.getS();
      set(qx, qy, qz, qs);
   }

   /**
    * Performs a linear interpolation in SO(3) from {@code q0} to {@code qf} given the percentage
    * {@code alpha}.
    * <p>
    * The interpolation method used here is often called a <i>Spherical Linear Interpolation</i> or
    * SLERP.
    * </p>
    *
    * @param q0 the first quaternion used in the interpolation. Not modified.
    * @param qf the second quaternion used in the interpolation. Not modified.
    * @param alpha the percentage to use for the interpolation. A value of 0 will result in setting
    *           this quaternion to {@code q0}, while a value of 1 is equivalent to setting this
    *           quaternion to {@code qf}.
    */
   public final void interpolate(QuaternionReadOnly q0, FrameQuaternionReadOnly qf, double alpha)
   {
      double cosHalfTheta = q0.dot(qf);
      double sign = 1.0;

      if (cosHalfTheta < 0.0)
      {
         sign = -1.0;
         cosHalfTheta = -cosHalfTheta;
      }

      double alpha0 = 1.0 - alpha;
      double alphaf = alpha;

      if (1.0 - cosHalfTheta > 1.0e-12)
      {
         double halfTheta = Math.acos(cosHalfTheta);
         double sinHalfTheta = Math.sin(halfTheta);
         alpha0 = Math.sin(alpha0 * halfTheta) / sinHalfTheta;
         alphaf = Math.sin(alphaf * halfTheta) / sinHalfTheta;
      }

      double qx = alpha0 * q0.getX() + sign * alphaf * qf.getX();
      double qy = alpha0 * q0.getY() + sign * alphaf * qf.getY();
      double qz = alpha0 * q0.getZ() + sign * alphaf * qf.getZ();
      double qs = alpha0 * q0.getS() + sign * alphaf * qf.getS();
      set(qx, qy, qz, qs);
   }

   /**
    * Performs a linear interpolation in SO(3) from {@code q0} to {@code qf} given the percentage
    * {@code alpha}.
    * <p>
    * The interpolation method used here is often called a <i>Spherical Linear Interpolation</i> or
    * SLERP.
    * </p>
    *
    * @param q0 the first quaternion used in the interpolation. Not modified.
    * @param qf the second quaternion used in the interpolation. Not modified.
    * @param alpha the percentage to use for the interpolation. A value of 0 will result in setting
    *           this quaternion to {@code q0}, while a value of 1 is equivalent to setting this
    *           quaternion to {@code qf}.
    * @throws ReferenceFrameMismatchException if {@code q0} is not expressed in the same
    *            reference frame as {@code qf}.
    */
   public final void interpolate(FrameQuaternionReadOnly q0, FrameQuaternionReadOnly qf, double alpha)
   {
      q0.checkReferenceFrameMatch(qf);

      double cosHalfTheta = q0.dot(qf);
      double sign = 1.0;

      if (cosHalfTheta < 0.0)
      {
         sign = -1.0;
         cosHalfTheta = -cosHalfTheta;
      }

      double alpha0 = 1.0 - alpha;
      double alphaf = alpha;

      if (1.0 - cosHalfTheta > 1.0e-12)
      {
         double halfTheta = Math.acos(cosHalfTheta);
         double sinHalfTheta = Math.sin(halfTheta);
         alpha0 = Math.sin(alpha0 * halfTheta) / sinHalfTheta;
         alphaf = Math.sin(alphaf * halfTheta) / sinHalfTheta;
      }

      double qx = alpha0 * q0.getX() + sign * alphaf * qf.getX();
      double qy = alpha0 * q0.getY() + sign * alphaf * qf.getY();
      double qz = alpha0 * q0.getZ() + sign * alphaf * qf.getZ();
      double qs = alpha0 * q0.getS() + sign * alphaf * qf.getS();
      set(qx, qy, qz, qs);
   }

   /**
    * Multiplies this quaternion by {@code other}.
    * <p>
    * this = this * other
    * </p>
    *
    * @param other the other quaternion to multiply this. Not modified.
    * @throws ReferenceFrameMismatchException if {@code other} is not expressed in the same
    *            reference frame as {@code this}.
    */
   public final void multiply(FrameQuaternionReadOnly other)
   {
      checkReferenceFrameMatch(other);
      QuaternionTools.multiply(this, other, this);
   }

   /**`
    * Sets this quaternion to the multiplication of {@code q1} and {@code q2}.
    * <p>
    * this = q1 * q2
    * </p>
    *
    * @param q1 the first quaternion in the multiplication. Not modified.
    * @param q2 the second quaternion in the multiplication. Not modified.
    */
   public final void multiply(FrameQuaternionReadOnly q1, QuaternionReadOnly q2)
   {
      QuaternionTools.multiply(q1, q2, this);
   }

   /**`
    * Sets this quaternion to the multiplication of {@code q1} and {@code q2}.
    * <p>
    * this = q1 * q2
    * </p>
    *
    * @param q1 the first quaternion in the multiplication. Not modified.
    * @param q2 the second quaternion in the multiplication. Not modified.
    */
   public final void multiply(QuaternionReadOnly q1, FrameQuaternionReadOnly q2)
   {
      QuaternionTools.multiply(q1, q2, this);
   }

   /**`
    * Sets this quaternion to the multiplication of {@code q1} and {@code q2}.
    * <p>
    * this = q1 * q2
    * </p>
    *
    * @param q1 the first quaternion in the multiplication. Not modified.
    * @param q2 the second quaternion in the multiplication. Not modified.
    * @throws ReferenceFrameMismatchException if {@code q1} is not expressed in the same
    *            reference frame as {@code q2}.
    */
   public final void multiply(FrameQuaternionReadOnly q1, FrameQuaternionReadOnly q2)
   {
      q1.checkReferenceFrameMatch(q2);
      QuaternionTools.multiply(q1, q2, this);
   }

   /**
    * Sets this quaternion to the conjugate of {@code other}.
    *
    * <pre>
    *      / -qx \
    * q* = | -qy |
    *      | -qz |
    *      \  qs /
    * </pre>
    *
    * @param other the other quaternion to copy the values from. Not modified.
    * @throws ReferenceFrameMismatchException if {@code other} is not expressed in the same
    *            reference frame as {@code this}.
    */
   public final void setAndConjugate(FrameQuaternionReadOnly other)
   {
      checkReferenceFrameMatch(other);
      set((QuaternionReadOnly)other);
      conjugate();
   }

   /**
    * Sets this quaternion to the inverse of {@code other}.
    *
    * @param other the other quaternion to copy the values from. Not modified.
    */
   public final void setAndInverse(FrameQuaternionReadOnly other)
   {
      checkReferenceFrameMatch(other);
      set((QuaternionReadOnly)other);
      inverse();
   }

   /**
    * Sets this quaternion to represent the same orientation as the given Euler angles
    * {@code eulerAngles}.
    * <p>
    * This is equivalent to
    * {@code this.setYawPitchRoll(eulerAngles.getZ(), eulerAngles.getY(), eulerAngles.getX())}.
    * </p>
    *
    * @param eulerAngles the Euler angles to copy the orientation from. Not modified.
    * @throws ReferenceFrameMismatchException if {@code eulerAngles} is not expressed in the same
    *            reference frame as {@code this}.
    */
   public final void setEuler(FrameVector3DReadOnly eulerAngles)
   {
      checkReferenceFrameMatch(eulerAngles);
      QuaternionConversion.convertYawPitchRollToQuaternion(eulerAngles.getZ(), eulerAngles.getY(), eulerAngles.getX(), this);
   }

   /**
    * Gets the read-only reference to the quaternion used in {@code this}.
    *
    * @return the quaternion of {@code this}.
    */
   public final QuaternionReadOnly getQuaternion()
   {
      return tuple;
   }

   @Override
   public void setUnsafe(double qx, double qy, double qz, double qs)
   {
      tuple.setUnsafe(qx, qy, qz, qs);
   }
}
