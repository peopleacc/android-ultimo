import { ArrowLeft, MessageCircle, Phone, Mail, HelpCircle, FileText, Send } from "lucide-react";
import { Card } from "./ui/card";
import { Button } from "./ui/button";
import { Textarea } from "./ui/textarea";
import { Input } from "./ui/input";
import { Label } from "./ui/label";
import { useState } from "react";

interface HelpSupportScreenProps {
  onNavigate: (screen: string) => void;
}

export function HelpSupportScreen({ onNavigate }: HelpSupportScreenProps) {
  const [subject, setSubject] = useState("");
  const [message, setMessage] = useState("");

  const handleSubmit = () => {
    if (!subject || !message) {
      alert("Please fill in all fields");
      return;
    }
    alert("Your message has been sent! We'll get back to you soon.");
    setSubject("");
    setMessage("");
  };

  const contactMethods = [
    {
      icon: Phone,
      title: "Phone Support",
      description: "+62 21 1234 5678",
      subtitle: "Mon - Sat, 9:00 AM - 6:00 PM",
      action: "tel:+622112345678",
    },
    {
      icon: Mail,
      title: "Email Support",
      description: "support@jokseat.com",
      subtitle: "Response within 24 hours",
      action: "mailto:support@jokseat.com",
    },
    {
      icon: MessageCircle,
      title: "WhatsApp",
      description: "+62 812-3456-7890",
      subtitle: "Fast response via chat",
      action: "https://wa.me/6281234567890",
    },
  ];

  const faqItems = [
    {
      question: "Berapa lama waktu pengerjaan?",
      answer: "Waktu pengerjaan standar adalah 3-4 hari. Kami juga menyediakan layanan express (1-2 hari) dan same day (6-8 jam) dengan biaya tambahan.",
    },
    {
      question: "Apakah garansi tersedia?",
      answer: "Ya, semua pemasangan jok kami dilengkapi dengan garansi 1 tahun untuk jahitan dan material.",
    },
    {
      question: "Bagaimana cara pembayaran?",
      answer: "Kami menerima pembayaran melalui QRIS dan cash. Pembayaran dilakukan setelah pengerjaan selesai.",
    },
    {
      question: "Apakah bisa custom desain?",
      answer: "Ya, kami menerima custom desain sesuai keinginan Anda. Silakan hubungi customer service untuk konsultasi lebih lanjut.",
    },
  ];

  return (
    <div className="min-h-screen bg-[#FFF2F2] pb-24">
      {/* Header */}
      <div className="bg-gradient-to-r from-[#2D336B] to-[#4A5194] text-white p-6 sticky top-0 z-10 shadow-lg">
        <div className="flex items-center gap-3">
          <button onClick={() => onNavigate("profile")}>
            <ArrowLeft className="w-6 h-6" />
          </button>
          <h1 className="text-xl">Help & Support</h1>
        </div>
      </div>

      {/* Content */}
      <div className="px-6 mt-6">
        {/* Contact Methods */}
        <h2 className="mb-3 text-[#2D336B]">Contact Us</h2>
        <div className="space-y-3 mb-6">
          {contactMethods.map((method, index) => {
            const Icon = method.icon;
            return (
              <Card
                key={index}
                className="p-4 cursor-pointer hover:shadow-md transition-shadow border-[#E8E9F3]"
                onClick={() => window.open(method.action, "_blank")}
              >
                <div className="flex items-center gap-3">
                  <div className="w-12 h-12 bg-[#E8E9F3] rounded-xl flex items-center justify-center flex-shrink-0">
                    <Icon className="w-6 h-6 text-[#2D336B]" />
                  </div>
                  <div className="flex-1">
                    <p className="text-sm text-[#2D336B] mb-1">{method.title}</p>
                    <p className="text-gray-900 mb-1">{method.description}</p>
                    <p className="text-xs text-gray-600">{method.subtitle}</p>
                  </div>
                </div>
              </Card>
            );
          })}
        </div>

        {/* FAQ Section */}
        <h2 className="mb-3 text-[#2D336B]">Frequently Asked Questions</h2>
        <div className="space-y-3 mb-6">
          {faqItems.map((faq, index) => (
            <Card key={index} className="p-4 border-[#E8E9F3]">
              <div className="flex gap-3">
                <div className="flex-shrink-0 mt-1">
                  <HelpCircle className="w-5 h-5 text-[#2D336B]" />
                </div>
                <div>
                  <p className="text-sm text-[#2D336B] mb-2">{faq.question}</p>
                  <p className="text-sm text-gray-700">{faq.answer}</p>
                </div>
              </div>
            </Card>
          ))}
        </div>

        {/* Contact Form */}
        <h2 className="mb-3 text-[#2D336B]">Send Us a Message</h2>
        <Card className="p-6 border-[#E8E9F3]">
          <div className="space-y-4">
            <div>
              <Label htmlFor="subject" className="flex items-center gap-2 mb-2 text-[#2D336B]">
                <FileText className="w-4 h-4" />
                Subject
              </Label>
              <Input
                id="subject"
                placeholder="What can we help you with?"
                value={subject}
                onChange={(e) => setSubject(e.target.value)}
                className="border-[#E8E9F3] focus:border-[#2D336B]"
              />
            </div>

            <div>
              <Label htmlFor="message" className="flex items-center gap-2 mb-2 text-[#2D336B]">
                <MessageCircle className="w-4 h-4" />
                Message
              </Label>
              <Textarea
                id="message"
                placeholder="Describe your issue or question..."
                value={message}
                onChange={(e) => setMessage(e.target.value)}
                className="min-h-32 border-[#E8E9F3] focus:border-[#2D336B]"
              />
            </div>

            <Button
              onClick={handleSubmit}
              className="w-full bg-gradient-to-r from-[#2D336B] to-[#4A5194] hover:from-[#1A1D3F] hover:to-[#2D336B] text-white h-12 rounded-xl"
            >
              <Send className="w-5 h-5 mr-2" />
              Send Message
            </Button>
          </div>
        </Card>

        {/* Additional Info */}
        <Card className="p-4 mt-6 bg-[#E8E9F3] border-[#E8E9F3]">
          <div className="text-center">
            <p className="text-sm text-[#2D336B] mb-2">Need immediate assistance?</p>
            <p className="text-xs text-gray-600">
              Our customer service team is available Monday - Saturday, 9:00 AM - 6:00 PM
            </p>
          </div>
        </Card>
      </div>
    </div>
  );
}
