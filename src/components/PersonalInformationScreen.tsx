import { ArrowLeft, User, Mail, Phone, MapPin, Calendar, Save } from "lucide-react";
import { Card } from "./ui/card";
import { Button } from "./ui/button";
import { Input } from "./ui/input";
import { Label } from "./ui/label";
import { Textarea } from "./ui/textarea";
import { Avatar, AvatarFallback } from "./ui/avatar";
import { useState } from "react";

interface PersonalInformationScreenProps {
  onNavigate: (screen: string) => void;
}

export function PersonalInformationScreen({ onNavigate }: PersonalInformationScreenProps) {
  const [formData, setFormData] = useState({
    name: "John Doe",
    email: "john.doe@example.com",
    phone: "+62 812-3456-7890",
    address: "Jl. Gatot Subroto No. 123, Jakarta",
    dateOfBirth: "1990-01-15",
  });

  const handleChange = (field: string, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const handleSave = () => {
    // Save logic here
    alert("Profile updated successfully!");
    onNavigate("profile");
  };

  return (
    <div className="min-h-screen bg-[#FFF2F2] pb-24">
      {/* Header */}
      <div className="bg-gradient-to-r from-[#2D336B] to-[#4A5194] text-white p-6 sticky top-0 z-10 shadow-lg">
        <div className="flex items-center gap-3">
          <button onClick={() => onNavigate("profile")}>
            <ArrowLeft className="w-6 h-6" />
          </button>
          <h1 className="text-xl">Personal Information</h1>
        </div>
      </div>

      {/* Content */}
      <div className="px-6 mt-6">
        {/* Profile Picture Section */}
        <div className="flex flex-col items-center mb-6">
          <Avatar className="w-24 h-24 border-4 border-[#2D336B] mb-4">
            <AvatarFallback className="bg-gradient-to-br from-[#2D336B] to-[#4A5194] text-white text-3xl">
              {formData.name
                .split(" ")
                .map((n) => n[0])
                .join("")}
            </AvatarFallback>
          </Avatar>
          <Button
            variant="outline"
            className="border-[#2D336B] text-[#2D336B] hover:bg-[#E8E9F3]"
          >
            Change Photo
          </Button>
        </div>

        {/* Form */}
        <Card className="p-6 border-[#E8E9F3]">
          <div className="space-y-5">
            {/* Full Name */}
            <div>
              <Label htmlFor="name" className="flex items-center gap-2 mb-2 text-[#2D336B]">
                <User className="w-4 h-4" />
                Full Name
              </Label>
              <Input
                id="name"
                value={formData.name}
                onChange={(e) => handleChange("name", e.target.value)}
                className="border-[#E8E9F3] focus:border-[#2D336B]"
              />
            </div>

            {/* Email */}
            <div>
              <Label htmlFor="email" className="flex items-center gap-2 mb-2 text-[#2D336B]">
                <Mail className="w-4 h-4" />
                Email Address
              </Label>
              <Input
                id="email"
                type="email"
                value={formData.email}
                onChange={(e) => handleChange("email", e.target.value)}
                className="border-[#E8E9F3] focus:border-[#2D336B]"
              />
            </div>

            {/* Phone */}
            <div>
              <Label htmlFor="phone" className="flex items-center gap-2 mb-2 text-[#2D336B]">
                <Phone className="w-4 h-4" />
                Phone Number
              </Label>
              <Input
                id="phone"
                type="tel"
                value={formData.phone}
                onChange={(e) => handleChange("phone", e.target.value)}
                className="border-[#E8E9F3] focus:border-[#2D336B]"
              />
            </div>

            {/* Date of Birth */}
            <div>
              <Label htmlFor="dob" className="flex items-center gap-2 mb-2 text-[#2D336B]">
                <Calendar className="w-4 h-4" />
                Date of Birth
              </Label>
              <Input
                id="dob"
                type="date"
                value={formData.dateOfBirth}
                onChange={(e) => handleChange("dateOfBirth", e.target.value)}
                className="border-[#E8E9F3] focus:border-[#2D336B]"
              />
            </div>

            {/* Address */}
            <div>
              <Label htmlFor="address" className="flex items-center gap-2 mb-2 text-[#2D336B]">
                <MapPin className="w-4 h-4" />
                Address
              </Label>
              <Textarea
                id="address"
                value={formData.address}
                onChange={(e) => handleChange("address", e.target.value)}
                className="min-h-24 border-[#E8E9F3] focus:border-[#2D336B]"
              />
            </div>
          </div>
        </Card>

        {/* Save Button */}
        <Button
          onClick={handleSave}
          className="w-full mt-6 bg-gradient-to-r from-[#2D336B] to-[#4A5194] hover:from-[#1A1D3F] hover:to-[#2D336B] text-white h-12 rounded-xl"
        >
          <Save className="w-5 h-5 mr-2" />
          Save Changes
        </Button>
      </div>
    </div>
  );
}
