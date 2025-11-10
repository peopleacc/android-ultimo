import { Bell, ChevronRight, Armchair, Clock, Wrench, Star, Sparkles } from "lucide-react";
import { Card } from "./ui/card";
import { Badge } from "./ui/badge";
import { Button } from "./ui/button";
import { ImageWithFallback } from "./figma/ImageWithFallback";

interface HomeScreenProps {
  onNavigate: (screen: string) => void;
}

export function HomeScreen({ onNavigate }: HomeScreenProps) {
  const seatDesigns = [
    { 
      id: 1, 
      name: "Premium Leather Black", 
      price: "Rp 3.500.000",
      image: "https://images.unsplash.com/photo-1741088088676-45ea7f7aafbc?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxsdXh1cnklMjBjYXIlMjBsZWF0aGVyJTIwc2VhdHN8ZW58MXx8fHwxNzYyNDM5MDQwfDA&ixlib=rb-4.1.0&q=80&w=1080",
      badge: "Best Seller",
      badgeColor: "bg-yellow-500"
    },
    { 
      id: 2, 
      name: "Sporty Red Accent", 
      price: "Rp 4.200.000",
      image: "https://images.unsplash.com/photo-1761846787427-cf2543833b51?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxjYXIlMjBpbnRlcmlvciUyMGN1c3RvbSUyMHNlYXRzfGVufDF8fHx8MTc2MjQzOTA0MHww&ixlib=rb-4.1.0&q=80&w=1080",
      badge: "New",
      badgeColor: "bg-green-500"
    },
    { 
      id: 3, 
      name: "Luxury Beige Supreme", 
      price: "Rp 5.000.000",
      image: "https://images.unsplash.com/photo-1699078614960-82b2ebe8d896?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxwcmVtaXVtJTIwdmVoaWNsZSUyMHVwaG9sc3Rlcnl8ZW58MXx8fHwxNzYyNDM5MDQxfDA&ixlib=rb-4.1.0&q=80&w=1080",
      badge: "Premium",
      badgeColor: "bg-[#2D336B]"
    },
    { 
      id: 4, 
      name: "Racing Carbon Style", 
      price: "Rp 4.800.000",
      image: "https://images.unsplash.com/photo-1761846786943-631ce70851f9?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxtb2Rlcm4lMjBjYXIlMjBzZWF0cyUyMGRlc2lnbnxlbnwxfHx8fDE3NjI0MzkwNDF8MA&ixlib=rb-4.1.0&q=80&w=1080",
      badge: "Popular",
      badgeColor: "bg-orange-500"
    },
  ];

  const activeOrders = [
    {
      id: "ORD-001",
      service: "Premium Leather Installation",
      status: "In Progress",
      progress: 65,
      estimatedTime: "2 hours",
    },
  ];

  return (
    <div className="pb-20 bg-gradient-to-b from-[#FFF2F2] to-[#FFF2F2] min-h-screen">
      {/* Header */}
      <div className="bg-gradient-to-r from-[#2D336B] to-[#4A5194] text-white p-6 rounded-b-3xl shadow-lg">
        <div className="flex justify-between items-center mb-6">
          <div>
            <h1 className="text-2xl mb-1">Welcome Back! 👋</h1>
            <p className="text-[#FFF2F2]/80 text-sm">Let's upgrade your car seats</p>
          </div>
          <button className="bg-white/20 p-2 rounded-full relative">
            <Bell className="w-6 h-6" />
            <span className="absolute top-1 right-1 w-2 h-2 bg-red-500 rounded-full"></span>
          </button>
        </div>

        {/* Quick Stats */}
        <div className="grid grid-cols-3 gap-3">
          <div className="bg-white/20 backdrop-blur-sm rounded-xl p-3 text-center">
            <p className="text-2xl mb-1">5</p>
            <p className="text-xs text-[#FFF2F2]/80">Completed</p>
          </div>
          <div className="bg-white/20 backdrop-blur-sm rounded-xl p-3 text-center">
            <p className="text-2xl mb-1">1</p>
            <p className="text-xs text-[#FFF2F2]/80">Active</p>
          </div>
          <div className="bg-white/20 backdrop-blur-sm rounded-xl p-3 text-center">
            <div className="flex items-center justify-center gap-1 mb-1">
              <Star className="w-4 h-4 fill-yellow-400 text-yellow-400" />
              <p className="text-2xl">4.8</p>
            </div>
            <p className="text-xs text-[#FFF2F2]/80">Rating</p>
          </div>
        </div>
      </div>

      <div className="px-6 mt-6">
        {/* Active Order */}
        {activeOrders.length > 0 && (
          <div className="mb-6">
            <h2 className="mb-3">Active Order</h2>
            {activeOrders.map((order) => (
              <Card
                key={order.id}
                className="p-4 border-[#E8E9F3] shadow-md hover:shadow-lg transition-shadow cursor-pointer"
                onClick={() => onNavigate("tracking")}
              >
                <div className="flex justify-between items-start mb-3">
                  <div>
                    <p className="text-gray-600 text-sm mb-1">{order.id}</p>
                    <h3 className="text-lg">{order.service}</h3>
                  </div>
                  <Badge className="bg-[#E8E9F3] text-[#2D336B] border-[#E8E9F3]">
                    {order.status}
                  </Badge>
                </div>
                <div className="mb-2">
                  <div className="flex justify-between text-sm mb-1">
                    <span className="text-gray-600">Progress</span>
                    <span className="text-[#2D336B]">{order.progress}%</span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-2">
                    <div
                      className="bg-gradient-to-r from-[#2D336B] to-[#4A5194] h-2 rounded-full transition-all"
                      style={{ width: `${order.progress}%` }}
                    ></div>
                  </div>
                </div>
                <div className="flex items-center text-sm text-gray-600">
                  <Clock className="w-4 h-4 mr-1" />
                  Est. {order.estimatedTime} remaining
                </div>
              </Card>
            ))}
          </div>
        )}

        {/* Services */}
        <div className="mb-6">
          <div className="flex justify-between items-center mb-3">
            <div className="flex items-center gap-2">
              <Sparkles className="w-5 h-5 text-[#2D336B]" />
              <h2>Katalog Desain Jok</h2>
            </div>
            <button className="text-[#2D336B] text-sm flex items-center">
              Lihat Semua
              <ChevronRight className="w-4 h-4" />
            </button>
          </div>
          
          {/* Horizontal Scrollable Catalog */}
          <div className="overflow-x-auto pb-2 -mx-6 px-6">
            <div className="flex gap-4" style={{ width: 'max-content' }}>
              {seatDesigns.map((design) => (
                <Card
                  key={design.id}
                  className="relative overflow-hidden border-[#E8E9F3] hover:border-[#2D336B] transition-all cursor-pointer flex-shrink-0"
                  style={{ width: '280px', height: '320px' }}
                  onClick={() => onNavigate("order")}
                >
                  {/* Image */}
                  <div className="relative w-full h-48">
                    <ImageWithFallback
                      src={design.image}
                      alt={design.name}
                      className="w-full h-full object-cover"
                    />
                    {/* Gradient Overlay */}
                    <div className="absolute inset-0 bg-gradient-to-t from-black/60 via-black/20 to-transparent"></div>
                    
                    {/* Badge */}
                    <div className="absolute top-3 right-3">
                      <Badge className={`${design.badgeColor} text-white border-0`}>
                        {design.badge}
                      </Badge>
                    </div>
                  </div>
                  
                  {/* Content */}
                  <div className="p-4">
                    <h3 className="mb-2">{design.name}</h3>
                    <p className="text-sm text-gray-600 mb-3">Mulai dari</p>
                    <p className="text-[#2D336B] mb-3">{design.price}</p>
                    <Button 
                      className="w-full bg-[#2D336B] hover:bg-[#1A1D3F] text-white h-9"
                      onClick={(e) => {
                        e.stopPropagation();
                        onNavigate("order");
                      }}
                    >
                      Pilih Desain
                    </Button>
                  </div>
                </Card>
              ))}
            </div>
          </div>
          
          {/* Scroll Indicator */}
          <p className="text-xs text-center text-gray-400 mt-2">← Geser untuk melihat lebih banyak →</p>
        </div>

        {/* CTA Button */}
        <Button
          className="w-full bg-gradient-to-r from-[#2D336B] to-[#4A5194] hover:from-[#1A1D3F] hover:to-[#2D336B] text-white h-12 rounded-xl shadow-lg"
          onClick={() => onNavigate("order")}
        >
          <Armchair className="w-5 h-5 mr-2" />
          Order New Service
        </Button>
      </div>
    </div>
  );
}