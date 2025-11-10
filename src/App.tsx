import { useState } from "react";
import { LoginScreen } from "./components/LoginScreen";
import { RegisterScreen } from "./components/RegisterScreen";
import { HomeScreen } from "./components/HomeScreen";
import { ServiceOrderScreen } from "./components/ServiceOrderScreen";
import { OrderTrackingScreen } from "./components/OrderTrackingScreen";
import { OrderHistoryScreen } from "./components/OrderHistoryScreen";
import { PaymentScreen } from "./components/PaymentScreen";
import { ProfileScreen } from "./components/ProfileScreen";
import { PersonalInformationScreen } from "./components/PersonalInformationScreen";
import { HelpSupportScreen } from "./components/HelpSupportScreen";
import { BottomNavigation } from "./components/BottomNavigation";

export default function App() {
  const [currentScreen, setCurrentScreen] = useState("login");
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  const handleNavigate = (screen: string) => {
    setCurrentScreen(screen);
  };

  const handleLogin = () => {
    setIsAuthenticated(true);
    setCurrentScreen("home");
  };

  const handleRegister = () => {
    setIsAuthenticated(true);
    setCurrentScreen("home");
  };

  const handleLogout = () => {
    setIsAuthenticated(false);
    setCurrentScreen("login");
  };

  // If not authenticated, show login/register screens
  if (!isAuthenticated) {
    if (currentScreen === "register") {
      return <RegisterScreen onNavigate={handleNavigate} onRegister={handleRegister} />;
    }
    return <LoginScreen onNavigate={handleNavigate} onLogin={handleLogin} />;
  }

  const renderScreen = () => {
    switch (currentScreen) {
      case "home":
        return <HomeScreen onNavigate={handleNavigate} />;
      case "order":
        return <ServiceOrderScreen onNavigate={handleNavigate} />;
      case "tracking":
        return <OrderTrackingScreen onNavigate={handleNavigate} />;
      case "orders":
        return <OrderHistoryScreen onNavigate={handleNavigate} />;
      case "payment":
        return <PaymentScreen onNavigate={handleNavigate} />;
      case "profile":
        return <ProfileScreen onNavigate={handleNavigate} onLogout={handleLogout} />;
      case "personal-info":
        return <PersonalInformationScreen onNavigate={handleNavigate} />;
      case "help-support":
        return <HelpSupportScreen onNavigate={handleNavigate} />;
      default:
        return <HomeScreen onNavigate={handleNavigate} />;
    }
  };

  return (
    <div className="min-h-screen bg-white">
      {/* Mobile Container */}
      <div className="max-w-md mx-auto bg-white shadow-xl min-h-screen relative">
        {renderScreen()}
        
        {/* Bottom Navigation */}
        <BottomNavigation
          activeTab={
            currentScreen === "home"
              ? "home"
              : currentScreen === "orders"
              ? "orders"
              : currentScreen === "tracking"
              ? "tracking"
              : currentScreen === "profile"
              ? "profile"
              : "home"
          }
          onTabChange={(tab) => {
            if (tab === "home") handleNavigate("home");
            else if (tab === "orders") handleNavigate("orders");
            else if (tab === "tracking") handleNavigate("tracking");
            else if (tab === "profile") handleNavigate("profile");
          }}
        />
      </div>
    </div>
  );
}