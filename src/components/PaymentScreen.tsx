import { ArrowLeft, QrCode, Banknote, CheckCircle, Copy } from "lucide-react";
import { useState } from "react";
import { Card } from "./ui/card";
import { Button } from "./ui/button";
import { RadioGroup, RadioGroupItem } from "./ui/radio-group";
import { Label } from "./ui/label";

interface PaymentScreenProps {
  onNavigate: (screen: string) => void;
}

export function PaymentScreen({ onNavigate }: PaymentScreenProps) {
  const [paymentMethod, setPaymentMethod] = useState("qris");
  const [orderConfirmed, setOrderConfirmed] = useState(false);

  const orderDetails = {
    id: "ORD-001",
    service: "Premium Leather Installation",
    design: "Premium Leather",
    material: "Genuine Leather",
    serviceType: "Standard",
    totalPrice: 2500000,
    estimatedTime: "3-4 days",
  };

  const handleConfirmOrder = () => {
    setOrderConfirmed(true);
    setTimeout(() => {
      onNavigate("home");
    }, 2000);
  };

  if (orderConfirmed) {
    return (
      <div className="min-h-screen bg-gradient-to-b from-[#FFF2F2] to-[#FFF2F2] flex items-center justify-center p-6">
        <div className="text-center">
          <div className="w-24 h-24 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6 animate-bounce">
            <CheckCircle className="w-16 h-16 text-green-600" />
          </div>
          <h1 className="text-2xl mb-3 text-gray-900">Order Confirmed!</h1>
          <p className="text-gray-600 mb-6">
            Your order has been successfully placed.
            <br />
            We'll notify you when the work begins.
          </p>
          <div className="bg-[#E8E9F3] rounded-xl p-4 inline-block">
            <p className="text-sm text-[#2D336B] mb-1">Order ID</p>
            <p className="text-xl text-[#2D336B]">{orderDetails.id}</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-b from-[#FFF2F2] to-[#FFF2F2] pb-48">
      {/* Header */}
      <div className="bg-gradient-to-r from-[#2D336B] to-[#4A5194] text-white p-6 sticky top-0 z-10 shadow-lg">
        <div className="flex items-center gap-3">
          <button onClick={() => onNavigate("order")}>
            <ArrowLeft className="w-6 h-6" />
          </button>
          <h1 className="text-xl">Payment</h1>
        </div>
      </div>

      <div className="px-6 mt-6">
        {/* Order Summary */}
        <Card className="p-4 mb-6 border-[#E8E9F3]">
          <h2 className="mb-4 text-[#2D336B]">Order Summary</h2>
          <div className="space-y-3">
            <div className="flex justify-between text-sm">
              <span className="text-gray-600">Order ID</span>
              <span className="text-gray-900">{orderDetails.id}</span>
            </div>
            <div className="flex justify-between text-sm">
              <span className="text-gray-600">Service</span>
              <span className="text-gray-900">{orderDetails.service}</span>
            </div>
            <div className="flex justify-between text-sm">
              <span className="text-gray-600">Design</span>
              <span className="text-gray-900">{orderDetails.design}</span>
            </div>
            <div className="flex justify-between text-sm">
              <span className="text-gray-600">Material</span>
              <span className="text-gray-900">{orderDetails.material}</span>
            </div>
            <div className="flex justify-between text-sm">
              <span className="text-gray-600">Service Type</span>
              <span className="text-gray-900">{orderDetails.serviceType}</span>
            </div>
            <div className="flex justify-between text-sm">
              <span className="text-gray-600">Estimated Time</span>
              <span className="text-[#2D336B]">{orderDetails.estimatedTime}</span>
            </div>
            <div className="border-t border-[#E8E9F3] pt-3 flex justify-between">
              <span>Total Amount</span>
              <span className="text-xl text-[#2D336B]">
                Rp {orderDetails.totalPrice.toLocaleString("id-ID")}
              </span>
            </div>
          </div>
        </Card>

        {/* Payment Method Selection */}
        <h2 className="mb-4 text-gray-900">Select Payment Method</h2>
        <RadioGroup value={paymentMethod} onValueChange={setPaymentMethod}>
          <div className="space-y-3">
            <Card
              className={`p-4 cursor-pointer transition-all ${
                paymentMethod === "qris"
                  ? "border-2 border-[#2D336B] shadow-md"
                  : "border border-gray-200"
              }`}
              onClick={() => setPaymentMethod("qris")}
            >
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-3">
                  <div className="w-12 h-12 bg-[#E8E9F3] rounded-lg flex items-center justify-center">
                    <QrCode className="w-6 h-6 text-[#2D336B]" />
                  </div>
                  <div>
                    <RadioGroupItem value="qris" id="qris" className="sr-only" />
                    <Label htmlFor="qris" className="cursor-pointer">
                      <p className="mb-1">QRIS Payment</p>
                      <p className="text-sm text-gray-600">
                        Scan QR code with any e-wallet
                      </p>
                    </Label>
                  </div>
                </div>
                {paymentMethod === "qris" && (
                  <CheckCircle className="w-6 h-6 text-[#2D336B]" />
                )}
              </div>
            </Card>

            <Card
              className={`p-4 cursor-pointer transition-all ${
                paymentMethod === "cash"
                  ? "border-2 border-[#2D336B] shadow-md"
                  : "border border-gray-200"
              }`}
              onClick={() => setPaymentMethod("cash")}
            >
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-3">
                  <div className="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
                    <Banknote className="w-6 h-6 text-green-600" />
                  </div>
                  <div>
                    <RadioGroupItem value="cash" id="cash" className="sr-only" />
                    <Label htmlFor="cash" className="cursor-pointer">
                      <p className="mb-1">Cash Payment</p>
                      <p className="text-sm text-gray-600">
                        Pay when you pick up your vehicle
                      </p>
                    </Label>
                  </div>
                </div>
                {paymentMethod === "cash" && (
                  <CheckCircle className="w-6 h-6 text-[#2D336B]" />
                )}
              </div>
            </Card>
          </div>
        </RadioGroup>

        {/* Payment Instructions */}
        {paymentMethod === "qris" && (
          <Card className="p-4 mt-6 bg-[#E8E9F3] border-[#E8E9F3]">
            <h3 className="mb-3 text-[#2D336B]">QRIS Payment Instructions</h3>
            <div className="bg-white p-6 rounded-xl mb-4 flex items-center justify-center">
              <div className="w-48 h-48 bg-gray-200 rounded-lg flex items-center justify-center">
                <QrCode className="w-24 h-24 text-gray-400" />
              </div>
            </div>
            <div className="space-y-2 text-sm text-gray-600">
              <p>1. Open your e-wallet app</p>
              <p>2. Scan the QR code above</p>
              <p>3. Confirm payment amount</p>
              <p>4. Complete the transaction</p>
            </div>
            <div className="mt-4 p-3 bg-white rounded-lg flex items-center justify-between">
              <p className="text-sm text-gray-600">Transaction ID: TRX-20251102-001</p>
              <button className="text-[#2D336B]">
                <Copy className="w-4 h-4" />
              </button>
            </div>
          </Card>
        )}

        {paymentMethod === "cash" && (
          <Card className="p-4 mt-6 bg-green-50 border-green-200">
            <h3 className="mb-3 text-green-900">Cash Payment Instructions</h3>
            <div className="space-y-2 text-sm text-gray-600">
              <p>• Your order will be confirmed immediately</p>
              <p>• Prepare exact amount when picking up</p>
              <p>• Payment receipt will be provided on-site</p>
              <p>• You can also request an invoice via email</p>
            </div>
          </Card>
        )}
      </div>

      {/* Bottom Action Bar */}
      <div className="fixed bottom-16 left-0 right-0 bg-[#FFF2F2] border-t border-gray-200 p-4 shadow-lg z-40">
        <div className="max-w-md mx-auto">
          <Button
            className="w-full bg-gradient-to-r from-[#2D336B] to-[#4A5194] hover:from-[#1A1D3F] hover:to-[#2D336B] text-white h-12 rounded-xl"
            onClick={handleConfirmOrder}
          >
            <CheckCircle className="w-5 h-5 mr-2" />
            Confirm Order & Payment Method
          </Button>
        </div>
      </div>
    </div>
  );
}
