import { ArrowLeft, User, Mail, Phone, MapPin, Bell, Shield, HelpCircle, LogOut, ChevronRight, Edit } from "lucide-react";
import { Card } from "./ui/card";
import { Switch } from "./ui/switch";
import { Avatar, AvatarFallback } from "./ui/avatar";

interface ProfileScreenProps {
  onNavigate: (screen: string) => void;
  onLogout?: () => void;
}

export function ProfileScreen({ onNavigate, onLogout }: ProfileScreenProps) {
  const userProfile = {
    name: "John Doe",
    email: "john.doe@example.com",
    phone: "+62 812-3456-7890",
    address: "Jl. Gatot Subroto No. 123, Jakarta",
    memberSince: "Jan 2024",
    totalOrders: 5,
    completedOrders: 4,
  };

  const menuItems = [
    {
      id: 1,
      icon: User,
      title: "Personal Information",
      description: "Update your personal details",
      action: "edit-profile",
    },
    {
      id: 2,
      icon: Bell,
      title: "Notifications",
      description: "Manage notification preferences",
      action: "notifications",
      toggle: true,
      value: true,
    },
    {
      id: 3,
      icon: HelpCircle,
      title: "Help & Support",
      description: "Get help with your orders",
      action: "support",
    },
  ];

  return (
    <div className="min-h-screen bg-gradient-to-b from-[#FFF2F2] to-[#FFF2F2] pb-20">
      {/* Header */}
      <div className="bg-gradient-to-r from-[#2D336B] to-[#4A5194] text-white p-6 rounded-b-3xl shadow-lg">
        <div className="flex items-center gap-3 mb-6">
          <button onClick={() => onNavigate("home")}>
            <ArrowLeft className="w-6 h-6" />
          </button>
          <h1 className="text-xl">Profile</h1>
        </div>

        {/* Profile Card */}
        <Card className="bg-white/95 backdrop-blur-sm p-6 border-0 shadow-md">
          <div className="flex items-center gap-4 mb-4">
            <Avatar className="w-20 h-20 border-4 border-[#E8E9F3]">
              <AvatarFallback className="bg-gradient-to-br from-[#2D336B] to-[#4A5194] text-white text-2xl">
                {userProfile.name
                  .split(" ")
                  .map((n) => n[0])
                  .join("")}
              </AvatarFallback>
            </Avatar>
            <div className="flex-1">
              <h2 className="text-xl text-gray-900 mb-1">{userProfile.name}</h2>
              <p className="text-sm text-gray-600">Member since {userProfile.memberSince}</p>
            </div>
            <button className="text-[#2D336B]">
              <Edit className="w-5 h-5" />
            </button>
          </div>

          <div className="grid grid-cols-2 gap-3 pt-4 border-t border-gray-200">
            <div className="text-center">
              <p className="text-2xl text-[#2D336B] mb-1">{userProfile.totalOrders}</p>
              <p className="text-xs text-gray-600">Total Orders</p>
            </div>
            <div className="text-center">
              <p className="text-2xl text-[#2D336B] mb-1">{userProfile.completedOrders}</p>
              <p className="text-xs text-gray-600">Completed</p>
            </div>
          </div>
        </Card>
      </div>

      <div className="px-6 mt-6">
        {/* Contact Information */}
        <h2 className="mb-3 text-gray-900">Contact Information</h2>
        <Card className="p-4 mb-6 border-[#E8E9F3]">
          <div className="space-y-3">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-[#E8E9F3] rounded-lg flex items-center justify-center flex-shrink-0">
                <Mail className="w-5 h-5 text-[#2D336B]" />
              </div>
              <div className="flex-1">
                <p className="text-xs text-gray-600 mb-1">Email</p>
                <p className="text-sm text-gray-900">{userProfile.email}</p>
              </div>
            </div>
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-[#E8E9F3] rounded-lg flex items-center justify-center flex-shrink-0">
                <Phone className="w-5 h-5 text-[#2D336B]" />
              </div>
              <div className="flex-1">
                <p className="text-xs text-gray-600 mb-1">Phone</p>
                <p className="text-sm text-gray-900">{userProfile.phone}</p>
              </div>
            </div>
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-[#E8E9F3] rounded-lg flex items-center justify-center flex-shrink-0">
                <MapPin className="w-5 h-5 text-[#2D336B]" />
              </div>
              <div className="flex-1">
                <p className="text-xs text-gray-600 mb-1">Address</p>
                <p className="text-sm text-gray-900">{userProfile.address}</p>
              </div>
            </div>
          </div>
        </Card>

        {/* Settings Menu */}
        <h2 className="mb-3 text-gray-900">Settings</h2>
        <div className="space-y-3">
          {menuItems.map((item) => {
            const Icon = item.icon;
            return (
              <Card
                key={item.id}
                className="p-4 hover:shadow-md transition-shadow cursor-pointer border-[#E8E9F3]"
                onClick={() => {
                  if (item.action === "edit-profile") {
                    onNavigate("personal-info");
                  } else if (item.action === "support") {
                    onNavigate("help-support");
                  }
                }}
              >
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 bg-[#E8E9F3] rounded-lg flex items-center justify-center flex-shrink-0">
                    <Icon className="w-5 h-5 text-[#2D336B]" />
                  </div>
                  <div className="flex-1">
                    <p className="text-sm text-gray-900 mb-1">{item.title}</p>
                    <p className="text-xs text-gray-600">{item.description}</p>
                  </div>
                  {item.toggle ? (
                    <Switch defaultChecked={item.value} />
                  ) : (
                    <ChevronRight className="w-5 h-5 text-gray-400" />
                  )}
                </div>
              </Card>
            );
          })}
        </div>

        {/* Logout Button */}
        <Card 
          className="p-4 mt-6 border-red-200 hover:shadow-md transition-shadow cursor-pointer"
          onClick={() => {
            if (confirm("Are you sure you want to logout?")) {
              onLogout?.();
            }
          }}
        >
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-red-100 rounded-lg flex items-center justify-center flex-shrink-0">
              <LogOut className="w-5 h-5 text-red-600" />
            </div>
            <div className="flex-1">
              <p className="text-sm text-red-600">Logout</p>
            </div>
          </div>
        </Card>

        {/* Version Info */}
        <p className="text-center text-xs text-gray-400 mt-6 mb-4">Version 1.0.0</p>
      </div>
    </div>
  );
}