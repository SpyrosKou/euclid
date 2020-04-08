package us.ihmc.euclid.referenceFrame.interfaces;

import us.ihmc.euclid.Axis3D;
import us.ihmc.euclid.referenceFrame.polytope.interfaces.FrameConvexPolytope3DReadOnly;
import us.ihmc.euclid.referenceFrame.polytope.interfaces.FrameFace3DReadOnly;
import us.ihmc.euclid.shape.primitives.interfaces.BoxPolytope3DView;

public interface FrameBoxPolytope3DView extends BoxPolytope3DView, FrameConvexPolytope3DReadOnly
{
   @Override
   FrameFace3DReadOnly getXMaxFace();

   @Override
   FrameFace3DReadOnly getYMaxFace();

   @Override
   FrameFace3DReadOnly getZMaxFace();

   @Override
   default FrameFace3DReadOnly getMaxFace(Axis3D axis)
   {
      return (FrameFace3DReadOnly) BoxPolytope3DView.super.getMaxFace(axis);
   }

   @Override
   FrameFace3DReadOnly getXMinFace();

   @Override
   FrameFace3DReadOnly getYMinFace();

   @Override
   FrameFace3DReadOnly getZMinFace();

   @Override
   default FrameFace3DReadOnly getMinFace(Axis3D axis)
   {
      return (FrameFace3DReadOnly) BoxPolytope3DView.super.getMinFace(axis);
   }

   @Override
   FrameBox3DReadOnly getOwner();

   @Override
   default FixedFrameBox3DBasics copy()
   {
      return getOwner().copy();
   }

   @Override
   default FrameBoundingBox3DReadOnly getBoundingBox()
   {
      return getOwner().getBoundingBox();
   }

   @Override
   default FramePoint3DReadOnly getCentroid()
   {
      return getOwner().getCentroid();
   }
}
