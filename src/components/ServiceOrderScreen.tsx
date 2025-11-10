import { useState } from "react";
import { ArrowLeft, Check, ChevronRight } from "lucide-react";
import { Card } from "./ui/card";
import { Button } from "./ui/button";
import { RadioGroup, RadioGroupItem } from "./ui/radio-group";
import { Label } from "./ui/label";
import { Textarea } from "./ui/textarea";

interface ServiceOrderScreenProps {
  onNavigate: (screen: string) => void;
}

export function ServiceOrderScreen({ onNavigate }: ServiceOrderScreenProps) {
  const [step, setStep] = useState(1);
  const [selectedDesign, setSelectedDesign] = useState("leather-premium");
  const [selectedMaterial, setSelectedMaterial] = useState("genuine-leather");
  const [selectedService, setSelectedService] = useState("standard");
  const [notes, setNotes] = useState("");

  const designs = [
    { 
      id: "leather-premium", 
      name: "Premium Leather Black", 
      price: 3000000, 
      image: "🪑",
      description: "Material kulit premium atau kulit sintetis berkualitas tinggi dengan finishing hitam pekat, kesan mewah dan elegan. Warna hitam membuat interior lebih netral dan mudah dipadankan dengan warna mobil apa pun. Biasanya memiliki jahitan rapi, bantalan lebih tebal, dan mungkin fitur tambahan seperti ventilasi atau pemanas tergantung paket."
    },
    { 
      id: "sports-racing", 
      name: "Sporty Red Accent", 
      price: 4000000, 
      image: "🏎️",
      description: "Desain sporty dengan aksen warna merah (misalnya jahitan merah, panel merah di sisi jok) yang memberi kesan \"racing\" atau aktif. Material bisa kulit atau kulit sintetis, dengan kombinasi hitam/merah — memberikan kontras visual yang kuat."
    },
    { 
      id: "luxury-comfort", 
      name: "Luxury Beige Supreme", 
      price: 5000000, 
      image: "💺",
      description: "Warna beige (krem muda / sand / cokelat terang) dengan kesan sangat mewah dan cerah — memberi nuansa \"luxury\" dan interior yang terbuka. \"Supreme\" di sini bisa berarti material dan finishing top‑tier: kulit asli atau premium, mungkin dengan aksen jahitan, bordir, dan bantalan ekstra. Warnanya lebih ringan dari hitam, sehingga tampilan mobil terasa lebih high‑end dan \"premium lounge\"."
    },
    { 
      id: "racing-carbon", 
      name: "Racing Carbon Style", 
      price: 7000000, 
      image: "🏁",
      description: "Desain highly sporty dengan tampilan \"carbon fibre\" atau motif carbon, sering dengan shell model \"bucket seat\" atau sporty bucket yang mendukung stabilitas saat menikung. Bisa menggunakan rangka atau shell karbon (atau motif karbon), bantalan tipis, posisi duduk agak rendah/menyelam untuk kesan balap. Untuk di mobil harian, gaya \"carbon style\" bisa berarti motif karbon, jahitan merah, dan profil sporty tetapi bukan full racing shell FIA."
    },
  ];

  const materials = [
    { id: "genuine-leather", name: "Genuine Leather", quality: "Premium" },
    { id: "synthetic-leather", name: "Synthetic Leather", quality: "Standard" },
    { id: "premium-fabric", name: "Premium Fabric", quality: "High" },
    { id: "suede", name: "Suede Material", quality: "Luxury" },
  ];

  const serviceTypes = [
    { id: "standard", name: "Standard", duration: "3-4 days", price: 0 },
    { id: "express", name: "Express", duration: "1-2 days", price: 500000 },
    { id: "same-day", name: "Same Day", duration: "6-8 hours", price: 1000000 },
  ];

  const selectedDesignData = designs.find((d) => d.id === selectedDesign);
  const selectedMaterialData = materials.find((m) => m.id === selectedMaterial);
  const selectedServiceData = serviceTypes.find((s) => s.id === selectedService);
  const totalPrice = (selectedDesignData?.price || 0) + (selectedServiceData?.price || 0);
  const estimatedTime = selectedServiceData?.duration || "";

  const renderStep1 = () => (
    <div>
      <h2 className="mb-4 text-[#2D336B]">Choose Design</h2>
      <div className="grid grid-cols-2 gap-3">
        {designs.map((design) => (
          <Card
            key={design.id}
            className={`p-4 cursor-pointer transition-all ${
              selectedDesign === design.id
                ? "border-2 border-[#2D336B] shadow-lg"
                : "border border-gray-200"
            }`}
            onClick={() => setSelectedDesign(design.id)}
          >
            <div className="text-4xl text-center mb-2">{design.image}</div>
            <p className="text-sm text-center mb-1">{design.name}</p>
            <p className="text-center text-[#2D336B]">
              Rp {design.price.toLocaleString("id-ID")}
            </p>
            {selectedDesign === design.id && (
              <div className="flex justify-center mt-2">
                <div className="bg-[#2D336B] text-white rounded-full w-6 h-6 flex items-center justify-center">
                  <Check className="w-4 h-4" />
                </div>
              </div>
            )}
          </Card>
        ))}
      </div>
    </div>
  );

  const renderStep2 = () => (
    <div>
      <h2 className="mb-4 text-[#2D336B]">Design Details</h2>
      
      {/* Selected Design Info */}
      <Card className="p-4 mb-6 bg-[#E8E9F3] border-[#E8E9F3]">
        <div className="flex items-center gap-3 mb-3">
          <div className="text-3xl">{selectedDesignData?.image}</div>
          <div>
            <h3 className="text-[#2D336B] mb-1">{selectedDesignData?.name}</h3>
            <p className="text-sm text-[#2D336B]">
              Rp {selectedDesignData?.price.toLocaleString("id-ID")}
            </p>
          </div>
        </div>
        <p className="text-sm text-gray-700 leading-relaxed">
          {selectedDesignData?.description}
        </p>
      </Card>

      {/* Material Selection */}
      <h3 className="mb-3 text-[#2D336B]">Select Material</h3>
      <RadioGroup value={selectedMaterial} onValueChange={setSelectedMaterial}>
        <div className="space-y-3">
          {materials.map((material) => (
            <Card
              key={material.id}
              className={`p-4 cursor-pointer transition-all ${
                selectedMaterial === material.id
                  ? "border-2 border-[#2D336B] shadow-md"
                  : "border border-gray-200"
              }`}
              onClick={() => setSelectedMaterial(material.id)}
            >
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-3">
                  <RadioGroupItem value={material.id} id={material.id} />
                  <Label htmlFor={material.id} className="cursor-pointer">
                    <p className="mb-1">{material.name}</p>
                    <p className="text-sm text-gray-600">Quality: {material.quality}</p>
                  </Label>
                </div>
                {selectedMaterial === material.id && (
                  <Check className="w-5 h-5 text-[#2D336B]" />
                )}
              </div>
            </Card>
          ))}
        </div>
      </RadioGroup>
    </div>
  );

  const renderStep3 = () => (
    <div>
      <h2 className="mb-4 text-[#2D336B]">Service Type</h2>
      <RadioGroup value={selectedService} onValueChange={setSelectedService}>
        <div className="space-y-3">
          {serviceTypes.map((service) => (
            <Card
              key={service.id}
              className={`p-4 cursor-pointer transition-all ${
                selectedService === service.id
                  ? "border-2 border-[#2D336B] shadow-md"
                  : "border border-gray-200"
              }`}
              onClick={() => setSelectedService(service.id)}
            >
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-3">
                  <RadioGroupItem value={service.id} id={service.id} />
                  <Label htmlFor={service.id} className="cursor-pointer">
                    <p className="mb-1">{service.name}</p>
                    <p className="text-sm text-gray-600">{service.duration}</p>
                    {service.price > 0 && (
                      <p className="text-sm text-[#2D336B] mt-1">
                        +Rp {service.price.toLocaleString("id-ID")}
                      </p>
                    )}
                  </Label>
                </div>
                {selectedService === service.id && (
                  <Check className="w-5 h-5 text-[#2D336B]" />
                )}
              </div>
            </Card>
          ))}
        </div>
      </RadioGroup>
    </div>
  );

  const renderStep4 = () => (
    <div>
      <h2 className="mb-4 text-[#2D336B]">Additional Notes</h2>
      <Textarea
        placeholder="Any special requests or notes for your order..."
        value={notes}
        onChange={(e) => setNotes(e.target.value)}
        className="min-h-32 mb-4 border-[#E8E9F3] focus:border-[#2D336B]"
      />

      <Card className="p-4 bg-[#E8E9F3] border-[#E8E9F3] mb-4">
        <h3 className="mb-3 text-[#2D336B]">Order Summary</h3>
        <div className="space-y-2 text-sm">
          <div className="flex justify-between">
            <span className="text-gray-600">Design:</span>
            <span>{selectedDesignData?.name}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-gray-600">Material:</span>
            <span>{selectedMaterialData?.name}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-gray-600">Service Type:</span>
            <span>{selectedServiceData?.name}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-gray-600">Estimated Time:</span>
            <span>{estimatedTime}</span>
          </div>
          <div className="border-t border-[#2D336B]/20 pt-2 mt-2 flex justify-between">
            <span>Total Price:</span>
            <span className="text-[#2D336B]">
              Rp {totalPrice.toLocaleString("id-ID")}
            </span>
          </div>
        </div>
      </Card>
    </div>
  );

  return (
    <div className="min-h-screen bg-[#FFF2F2] pb-48">
      {/* Header */}
      <div className="bg-gradient-to-r from-[#2D336B] to-[#4A5194] text-white p-6 sticky top-0 z-10 shadow-lg">
        <div className="flex items-center gap-3 mb-4">
          <button onClick={() => (step > 1 ? setStep(step - 1) : onNavigate("home"))}>
            <ArrowLeft className="w-6 h-6" />
          </button>
          <h1 className="text-xl">New Service Order</h1>
        </div>

        {/* Progress Steps */}
        <div className="flex items-center justify-between">
          {[1, 2, 3, 4].map((s) => (
            <div key={s} className="flex items-center flex-1">
              <div
                className={`w-8 h-8 rounded-full flex items-center justify-center ${
                  s <= step ? "bg-white text-[#2D336B]" : "bg-white/30 text-white"
                }`}
              >
                {s < step ? <Check className="w-5 h-5" /> : s}
              </div>
              {s < 4 && (
                <div
                  className={`flex-1 h-1 mx-1 ${
                    s < step ? "bg-white" : "bg-white/30"
                  }`}
                ></div>
              )}
            </div>
          ))}
        </div>
      </div>

      {/* Content */}
      <div className="px-6 mt-6">
        {step === 1 && renderStep1()}
        {step === 2 && renderStep2()}
        {step === 3 && renderStep3()}
        {step === 4 && renderStep4()}
      </div>

      {/* Bottom Action Bar */}
      <div className="fixed bottom-16 left-0 right-0 bg-[#FFF2F2] border-t border-gray-200 p-4 shadow-lg z-40">
        <div className="max-w-md mx-auto">
          <div className="flex items-center justify-between mb-3">
            <div>
              <p className="text-sm text-gray-600">Total Price</p>
              <p className="text-xl text-[#2D336B]">
                Rp {totalPrice.toLocaleString("id-ID")}
              </p>
            </div>
            <div className="text-right">
              <p className="text-sm text-gray-600">Est. Time</p>
              <p className="text-sm">{estimatedTime}</p>
            </div>
          </div>
          <Button
            className="w-full bg-gradient-to-r from-[#2D336B] to-[#4A5194] hover:from-[#1A1D3F] hover:to-[#2D336B] text-white h-12 rounded-xl"
            onClick={() => {
              if (step < 4) {
                setStep(step + 1);
              } else {
                // Place order
                onNavigate("payment");
              }
            }}
          >
            {step < 4 ? (
              <>
                Continue
                <ChevronRight className="w-5 h-5 ml-2" />
              </>
            ) : (
              "Confirm Order"
            )}
          </Button>
        </div>
      </div>
    </div>
  );
}
