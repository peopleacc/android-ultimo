import { ArrowLeft, CheckCircle, Clock, Package, Wrench, Star, ChevronDown } from "lucide-react";
import { Card } from "./ui/card";
import { Badge } from "./ui/badge";
import { Progress } from "./ui/progress";
import { useState } from "react";
import { Button } from "./ui/button";

interface OrderTrackingScreenProps {
  onNavigate: (screen: string) => void;

export function OrderTrackingScreen({ onNavigate }: OrderTrackingScreenProps) {
export function OrderTrackingScreen({ onNavigate }: OrderTrackingScreenProps) {
    {
      id: "ORD-001",
      service: "Premium Leather Installation",
      status: "In Progress",
      progress: 65,
      currentStep: "Installation",
      estimatedTime: "2 hours",
      startDate: "Nov 2, 2025 09:00 AM",
      estimatedCompletion: "Nov 2, 2025 03:00 PM",
      timeline: [
        {
          id: 1,
          title: "Order Confirmed",
          description: "Your order has been confirmed",
          time: "09:00 AM",
          completed: true,
          icon: CheckCircle,
        },
        {
          id: 2,
          title: "Material Preparation",
          description: "Preparing materials for installation",
          time: "10:00 AM",
          completed: true,
          icon: Package,
        },
        {
          id: 3,
          title: "Installation In Progress",
          description: "Installing your new seat covers",
          time: "11:30 AM",
          completed: false,
          active: true,
          icon: Wrench,
        },
        {
          id: 4,
          title: "Quality Check",
          description: "Final inspection and quality assurance",
          time: "02:00 PM",
          completed: false,
          icon: Star,
        },
        {
          id: 5,
          title: "Ready for Pickup",
          description: "Your vehicle is ready",
          time: "03:00 PM",
          completed: false,
          icon: CheckCircle,
        },
      ],
    },
    {
      id: "ORD-002",
      service: "Custom Sporty Design",
      status: "Quality Check",
      progress: 85,
      currentStep: "Quality Check",
      estimatedTime: "1 hour",
      startDate: "Nov 2, 2025 08:00 AM",
      estimatedCompletion: "Nov 2, 2025 02:00 PM",
      timeline: [
        {
          id: 1,
          title: "Order Confirmed",
          description: "Your order has been confirmed",
          time: "08:00 AM",
          completed: true,
          icon: CheckCircle,
        },
        {
          id: 2,
          title: "Material Preparation",
          description: "Preparing materials for installation",
          time: "09:00 AM",
          completed: true,
          icon: Package,
        },
        {
          id: 3,
          title: "Installation In Progress",
          description: "Installing your new seat covers",
          time: "10:30 AM",
          completed: true,
          icon: Wrench,
        },
        {
          id: 4,
          title: "Quality Check",
          description: "Final inspection and quality assurance",
          time: "01:00 PM",
          completed: false,
          active: true,
          icon: Star,
        },
        {
          id: 5,
          title: "Ready for Pickup",
          description: "Your vehicle is ready",
          time: "02:00 PM",
          completed: false,
          icon: CheckCircle,
        },
      ],
    },
    {
      id: "ORD-003",
      service: "Luxury Beige Supreme",
      status: "Material Prep",
      progress: 35,
      currentStep: "Material Preparation",
      estimatedTime: "4 hours",
      startDate: "Nov 2, 2025 10:00 AM",
      estimatedCompletion: "Nov 2, 2025 05:00 PM",
      timeline: [
        {
          id: 1,
          title: "Order Confirmed",
          description: "Your order has been confirmed",
          time: "10:00 AM",
          completed: true,
          icon: CheckCircle,
        },
        {
          id: 2,
          title: "Material Preparation",
          description: "Preparing materials for installation",
          time: "11:00 AM",
          completed: false,
          active: true,
          icon: Package,
        },
        {
          id: 3,
          title: "Installation In Progress",
          description: "Installing your new seat covers",
          time: "12:30 PM",
          completed: false,
          icon: Wrench,
        },
        {
          id: 4,
          title: "Quality Check",
          description: "Final inspection and quality assurance",
          time: "03:00 PM",
          completed: false,
          icon: Star,
        },
        {
          id: 5,
          title: "Ready for Pickup",
          description: "Your vehicle is ready",
          time: "05:00 PM",
          completed: false,
          icon: CheckCircle,
        },
      ],
    },
  ];

  const [selectedOrderId, setSelectedOrderId] = useState(orders[0].id);
  const [selectedOrderId, setSelectedOrderId] = useState(orders[0].id);
  const [showOrderSelector, setShowOrderSelector] = useState(false);

  const selectedOrder = orders.find(o => o.id === selectedOrderId) || orders[0];

  return (
    <div className="min-h-screen bg-gradient-to-b from-[#FFF2F2] to-[#FFF2F2] pb-20">
      {/* Header */}
      <div className="bg-gradient-to-r from-[#2D336B] to-[#4A5194] text-white p-6 rounded-b-3xl shadow-lg">
        <div className="flex items-center gap-3 mb-6">
          <button onClick={() => onNavigate("home")}>
            <ArrowLeft className="w-6 h-6" />
          </button>
          <h1 className="text-xl">Order Tracking</h1>
        </div>

        {/* Order Selector - Multiple Orders */}
        {orders.length > 1 && (
          <div className="mb-4">
            <Button
              onClick={() => setShowOrderSelector(!showOrderSelector)}
              className="w-full bg-white/20 hover:bg-white/30 text-white border-0 h-12 justify-between"
            >
              <span>Pilih Pesanan ({orders.length} pesanan aktif)</span>
              <ChevronDown className={`w-5 h-5 transition-transform ${showOrderSelector ? 'rotate-180' : ''}`} />
            </Button>
            
            {showOrderSelector && (
              <div className="mt-2 space-y-2 max-h-60 overflow-y-auto">
                {orders.map((order) => (
                  <Card
                    key={order.id}
                    className={`p-3 cursor-pointer transition-all ${
                      selectedOrderId === order.id
                        ? 'bg-white border-2 border-[#2D336B]'
                        : 'bg-white/90 hover:bg-white'
                    }`}
                    onClick={() => {
                      setSelectedOrderId(order.id);
                      setShowOrderSelector(false);
                    }}
                  >
                    <div className="flex justify-between items-start">
                      <div>
                        <p className="text-sm text-gray-600 mb-1">{order.id}</p>
                        <p className="text-gray-900">{order.service}</p>
                      </div>
                      <Badge className={`
                        ${order.progress >= 80 ? 'bg-green-100 text-green-700' : 
                          order.progress >= 50 ? 'bg-blue-100 text-blue-700' : 
                          'bg-orange-100 text-orange-700'} border-0
                      `}>
                        {order.progress}%
                      </Badge>
                    </div>
                  </Card>
                ))}
              </div>
            )}
          </div>
        )}
        {orders.length > 1 && (
          <div className="mb-4">
            <Button
              onClick={() => setShowOrderSelector(!showOrderSelector)}
              className="w-full bg-white/20 hover:bg-white/30 text-white border-0 h-12 justify-between"
            >
              <span>Pilih Pesanan ({orders.length} pesanan aktif)</span>
              <ChevronDown className={`w-5 h-5 transition-transform ${showOrderSelector ? 'rotate-180' : ''}`} />
            </Button>
            
            {showOrderSelector && (
              <div className="mt-2 space-y-2 max-h-60 overflow-y-auto">
                {orders.map((order) => (
                  <Card
                    key={order.id}
                    className={`p-3 cursor-pointer transition-all ${
                      selectedOrderId === order.id
                        ? 'bg-white border-2 border-[#2D336B]'
                        : 'bg-white/90 hover:bg-white'
                    }`}
                    onClick={() => {
                      setSelectedOrderId(order.id);
                      setShowOrderSelector(false);
                    }}
                  >
                    <div className="flex justify-between items-start">
                      <div>
                        <p className="text-sm text-gray-600 mb-1">{order.id}</p>
                        <p className="text-gray-900">{order.service}</p>
                      </div>
                      <Badge className={`
                        ${order.progress >= 80 ? 'bg-green-100 text-green-700' : 
                          order.progress >= 50 ? 'bg-blue-100 text-blue-700' : 
                          'bg-orange-100 text-orange-700'} border-0
                      `}>
                        {order.progress}%
                      </Badge>
                    </div>
                  </Card>
                ))}
              </div>
            )}
          </div>
        )}

        {/* Order Info Card */}
        <Card className="bg-white/95 backdrop-blur-sm p-4 border-0 shadow-md">
          <div className="flex justify-between items-start mb-3">
            <div>
              <p className="text-sm text-gray-600 mb-1">{selectedOrder.id}</p>
              <h2 className="text-lg text-gray-900">{selectedOrder.service}</h2>
            </div>
            <Badge className="bg-[#E8E9F3] text-[#2D336B] border-[#E8E9F3]">
              {selectedOrder.status}
            </Badge>
          </div>

          <div className="mb-3">
            <div className="flex justify-between text-sm mb-2">
              <span className="text-gray-600">Overall Progress</span>
              <span className="text-[#2D336B]">{selectedOrder.progress}%</span>
            </div>
            <Progress value={selectedOrder.progress} className="h-3" />
          </div>

          <div className="grid grid-cols-2 gap-3 pt-3 border-t border-gray-200">
            <div>
              <p className="text-xs text-gray-600 mb-1">Current Step</p>
              <p className="text-sm text-gray-900">{selectedOrder.currentStep}</p>
            </div>
            <div className="text-right">
              <p className="text-xs text-gray-600 mb-1">Est. Remaining</p>
              <p className="text-sm text-[#2D336B]">{selectedOrder.estimatedTime}</p>
            </div>
          </div>
        </Card>
      </div>

      <div className="px-6 mt-6">
        {/* Timeline */}
        <h2 className="mb-4 text-gray-900">Progress Timeline</h2>
        <div className="relative">
          {/* Vertical Line */}
          <div className="absolute left-6 top-0 bottom-0 w-0.5 bg-gray-200"></div>

          {selectedOrder.timeline.map((item, index) => {
            const Icon = item.icon;
            return (
              <div key={item.id} className="relative flex gap-4 mb-6">
                {/* Icon */}
                <div
                  className={`relative z-10 w-12 h-12 rounded-full flex items-center justify-center flex-shrink-0 ${
                    item.completed
                      ? "bg-[#2D336B] text-white"
                      : item.active
                      ? "bg-[#E8E9F3] text-[#2D336B] border-2 border-[#2D336B]"
                      : "bg-gray-200 text-gray-400"
                  }`}
                >
                  <Icon className="w-6 h-6" />
                  {item.active && (
                    <span className="absolute -inset-1 rounded-full border-2 border-[#2D336B] animate-pulse"></span>
                  )}
                </div>

                {/* Content */}
                <Card
                  className={`flex-1 p-4 ${
                    item.completed
                      ? "border-[#E8E9F3] bg-[#E8E9F3]/30"
                      : item.active
                      ? "border-[#2D336B] bg-white shadow-md"
                      : "border-gray-200 bg-gray-50"
                  }`}
                >
                  <div className="flex justify-between items-start mb-2">
                    <h3
                      className={`${
                        item.completed || item.active ? "text-gray-900" : "text-gray-500"
                      }`}
                    >
                      {item.title}
                    </h3>
                    <span
                      className={`text-sm ${
                        item.completed || item.active ? "text-[#2D336B]" : "text-gray-400"
                      }`}
                    >
                      {item.time}
                    </span>
                  </div>
                  <p
                    className={`text-sm ${
                      item.completed || item.active ? "text-gray-600" : "text-gray-400"
                    }`}
                  >
                    {item.description}
                  </p>
                  {item.active && (
                    <div className="mt-3 flex items-center text-sm text-[#2D336B]">
                      <Clock className="w-4 h-4 mr-1 animate-pulse" />
                      Currently processing...
                    </div>
                  )}
                </Card>
              </div>
            );
          })}
        </div>

        {/* Estimated Completion */}
        <Card className="p-4 bg-gradient-to-r from-[#E8E9F3] to-[#FFF2F2] border-[#E8E9F3] mt-6">
          <div className="flex items-center gap-3">
            <div className="bg-[#2D336B] text-white p-3 rounded-full">
              <Clock className="w-6 h-6" />
            </div>
            <div>
              <p className="text-sm text-gray-600 mb-1">Estimated Completion</p>
              <p className="text-lg text-[#2D336B]">{selectedOrder.estimatedCompletion}</p>
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
}
