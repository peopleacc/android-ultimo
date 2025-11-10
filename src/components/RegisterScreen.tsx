import { useState } from "react";
import { ArrowLeft, Eye, EyeOff } from "lucide-react";
import { Button } from "./ui/button";
import { Input } from "./ui/input";
import { Checkbox } from "./ui/checkbox";

interface RegisterScreenProps {
  onNavigate: (screen: string) => void;
  onRegister: () => void;
}

export function RegisterScreen({ onNavigate, onRegister }: RegisterScreenProps) {
  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    phone: "",
    password: "",
  });
  const [showPassword, setShowPassword] = useState(false);
  const [agreeTerms, setAgreeTerms] = useState(false);

  const handleChange = (field: string, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const handleRegister = () => {
    if (!formData.fullName || !formData.email || !formData.phone || !formData.password) {
      alert("Mohon isi semua field");
      return;
    }

    if (!agreeTerms) {
      alert("Mohon setujui Syarat & Ketentuan");
      return;
    }

    alert("Registrasi berhasil! Selamat datang di ULTIMO!");
    onRegister();
  };

  return (
    <div className="min-h-screen bg-[#2D336B] flex items-center justify-center p-6">
      <div className="w-full max-w-md">
        <div className="bg-white rounded-[2.5rem] shadow-2xl overflow-hidden">
          {/* Top Section with Wave */}
          <div className="relative bg-gradient-to-br from-[#2D336B] to-[#4A5194] pt-8 pb-32 px-6">
            {/* Back Button */}
            <button
              onClick={() => onNavigate("login")}
              className="text-white flex items-center gap-2 mb-6"
            >
              <ArrowLeft className="w-5 h-5" />
              Back
            </button>

            {/* Wave Shape */}
            <div className="absolute bottom-0 left-0 right-0">
              <svg viewBox="0 0 1440 120" fill="none" xmlns="http://www.w3.org/2000/svg" className="w-full">
                <path
                  d="M0 0C240 80 480 120 720 100C960 80 1200 40 1440 60V120H0V0Z"
                  fill="#FFF2F2"
                />
              </svg>
            </div>

            {/* Logo */}
            <div className="relative z-10 text-center">
              <div className="inline-flex items-center justify-center w-20 h-20 bg-white rounded-2xl mb-4 shadow-lg">
                <svg width="40" height="40" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M8 32L12 8H20L24 32H8Z" fill="#2D336B"/>
                  <path d="M16 32L20 8H28L32 32H16Z" fill="#4A5194"/>
                  <circle cx="16" cy="20" r="3" fill="#FFF2F2"/>
                  <circle cx="24" cy="20" r="3" fill="#FFF2F2"/>
                </svg>
              </div>
              <h1 className="text-white text-3xl tracking-wider">ULTIMO</h1>
            </div>
          </div>

          {/* Form Section */}
          <div className="bg-[#FFF2F2] px-8 pt-8 pb-10">
            <h2 className="text-center text-gray-800 text-2xl mb-8">Create Account</h2>

            <div className="space-y-4">
              {/* Full Name */}
              <Input
                type="text"
                placeholder="Full Name"
                value={formData.fullName}
                onChange={(e) => handleChange("fullName", e.target.value)}
                className="h-12 bg-white border-gray-200 rounded-xl text-gray-700 placeholder:text-gray-400"
              />

              {/* Email */}
              <Input
                type="email"
                placeholder="Email"
                value={formData.email}
                onChange={(e) => handleChange("email", e.target.value)}
                className="h-12 bg-white border-gray-200 rounded-xl text-gray-700 placeholder:text-gray-400"
              />

              {/* Phone */}
              <Input
                type="tel"
                placeholder="Phone Number"
                value={formData.phone}
                onChange={(e) => handleChange("phone", e.target.value)}
                className="h-12 bg-white border-gray-200 rounded-xl text-gray-700 placeholder:text-gray-400"
              />

              {/* Password */}
              <div className="relative">
                <Input
                  type={showPassword ? "text" : "password"}
                  placeholder="Password"
                  value={formData.password}
                  onChange={(e) => handleChange("password", e.target.value)}
                  className="h-12 bg-white border-gray-200 rounded-xl text-gray-700 placeholder:text-gray-400 pr-12"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
                >
                  {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                </button>
              </div>

              {/* Terms */}
              <div className="flex items-start gap-3 pt-2">
                <Checkbox
                  id="terms"
                  checked={agreeTerms}
                  onCheckedChange={(checked) => setAgreeTerms(checked as boolean)}
                  className="mt-1 border-[#2D336B] data-[state=checked]:bg-[#2D336B]"
                />
                <label htmlFor="terms" className="text-sm text-gray-600 leading-relaxed">
                  I agree to the Terms & Conditions and Privacy Policy
                </label>
              </div>

              {/* Register Button */}
              <Button
                onClick={handleRegister}
                className="w-full h-12 bg-transparent border-2 border-[#2D336B] text-[#2D336B] hover:bg-[#2D336B] hover:text-white rounded-xl text-base mt-6 transition-all"
              >
                Sign Up
              </Button>

              {/* Login Link */}
              <p className="text-center text-sm text-gray-600">
                Already have an account?{" "}
                <button
                  onClick={() => onNavigate("login")}
                  className="text-[#2D336B] hover:underline"
                >
                  Login
                </button>
              </p>

              {/* Divider */}
              <div className="relative py-4">
                <div className="absolute inset-0 flex items-center">
                  <div className="w-full border-t border-gray-300"></div>
                </div>
                <div className="relative flex justify-center">
                  <span className="px-4 bg-[#FFF2F2] text-sm text-gray-500">OR</span>
                </div>
              </div>

              {/* Social Login */}
              <div className="flex justify-center gap-4 pt-2">
                <button className="w-11 h-11 rounded-full bg-[#1DA1F2] flex items-center justify-center hover:opacity-80 transition-opacity">
                  <svg className="w-5 h-5" fill="white" viewBox="0 0 24 24">
                    <path d="M23.953 4.57a10 10 0 01-2.825.775 4.958 4.958 0 002.163-2.723c-.951.555-2.005.959-3.127 1.184a4.92 4.92 0 00-8.384 4.482C7.69 8.095 4.067 6.13 1.64 3.162a4.822 4.822 0 00-.666 2.475c0 1.71.87 3.213 2.188 4.096a4.904 4.904 0 01-2.228-.616v.06a4.923 4.923 0 003.946 4.827 4.996 4.996 0 01-2.212.085 4.936 4.936 0 004.604 3.417 9.867 9.867 0 01-6.102 2.105c-.39 0-.779-.023-1.17-.067a13.995 13.995 0 007.557 2.209c9.053 0 13.998-7.496 13.998-13.985 0-.21 0-.42-.015-.63A9.935 9.935 0 0024 4.59z" />
                  </svg>
                </button>
                <button className="w-11 h-11 rounded-full bg-[#0A66C2] flex items-center justify-center hover:opacity-80 transition-opacity">
                  <svg className="w-5 h-5" fill="white" viewBox="0 0 24 24">
                    <path d="M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433c-1.144 0-2.063-.926-2.063-2.065 0-1.138.92-2.063 2.063-2.063 1.14 0 2.064.925 2.064 2.063 0 1.139-.925 2.065-2.064 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z" />
                  </svg>
                </button>
                <button className="w-11 h-11 rounded-full bg-[#1877F2] flex items-center justify-center hover:opacity-80 transition-opacity">
                  <svg className="w-5 h-5" fill="white" viewBox="0 0 24 24">
                    <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z" />
                  </svg>
                </button>
                <button className="w-11 h-11 rounded-full bg-white border-2 border-gray-200 flex items-center justify-center hover:bg-gray-50 transition-colors">
                  <svg className="w-5 h-5" viewBox="0 0 24 24">
                    <path
                      fill="#4285F4"
                      d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                    />
                    <path
                      fill="#34A853"
                      d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                    />
                    <path
                      fill="#FBBC05"
                      d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
                    />
                    <path
                      fill="#EA4335"
                      d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
                    />
                  </svg>
                </button>
              </div>

              <p className="text-center text-xs text-gray-500 mt-4">
                Sign up with another account
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
