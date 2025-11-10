import { ArrowLeft, Search, Filter, ChevronRight, QrCode, Banknote } from "lucide-react";
import { useState } from "react";
import { Card } from "./ui/card";
import { Badge } from "./ui/badge";
import { Input } from "./ui/input";
import { Button } from "./ui/button";

interface OrderHistoryScreenProps {
  onNavigate: (screen: string) => void;
}

export function OrderHistoryScreen({ onNavigate }: OrderHistoryScreenProps) {
  const [searchQuery, setSearchQuery] = useState("");
  const [filterStatus, setFilterStatus] = useState("all");

  const orders = [
    {
      id: "ORD-001",
      service: "Premium Leather Installation",
      date: "Nov 2, 2025",
      status: "In Progress",
      statusColor: "bg-blue-100 text-blue-600 border-blue-200",
      price: 2500000,
      paymentMethod: "QRIS",
      paymentStatus: "Paid",
      estimatedTime: "3-4 days",
      progress: 65,
    },
    {
      id: "ORD-002",
      service: "Sports Racing Design",
      date: "Oct 28, 2025",
      status: "Completed",
      statusColor: "bg-green-100 text-green-600 border-green-200",
      price: 3500000,
      paymentMethod: "Cash",
      paymentStatus: "Paid",
      estimatedTime: "3-4 days",
      progress: 100,
    },
    {
      id: "ORD-003",
      service: "Fabric Cover Standard",
      date: "Oct 15, 2025",
      status: "Completed",
      statusColor: "bg-green-100 text-green-600 border-green-200",
      price: 1800000,
      paymentMethod: "QRIS",
      paymentStatus: "Paid",
      estimatedTime: "2-3 days",
      progress: 100,
    },
    {
      id: "ORD-004",
      service: "Custom Design Premium",
      date: "Sep 20, 2025",
      status: "Completed",
      statusColor: "bg-green-100 text-green-600 border-green-200",
      price: 4200000,
      paymentMethod: "QRIS",
      paymentStatus: "Paid",
      estimatedTime: "4-5 days",
      progress: 100,
    },
  ];

  const filteredOrders = orders.filter((order) => {
    const matchesSearch =
      order.id.toLowerCase().includes(searchQuery.toLowerCase()) ||
      order.service.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesFilter =
      filterStatus === "all" || order.status.toLowerCase() === filterStatus.toLowerCase();
    return matchesSearch && matchesFilter;
  });

  return (
    <div className="min-h-screen bg-gradient-to-b from-[#FFF2F2] to-[#FFF2F2] pb-20">
      {/* Header */}
      <div className="bg-gradient-to-r from-[#2D336B] to-[#4A5194] text-white p-6 sticky top-0 z-10 shadow-lg">
        <div className="flex items-center gap-3 mb-4">
          <button onClick={() => onNavigate("home")}>
            <ArrowLeft className="w-6 h-6" />
          </button>
          <h1 className="text-xl">Order History</h1>
        </div>

        {/* Search Bar */}
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
          <Input
            type="text"
            placeholder="Search orders..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="pl-10 bg-white/95 border-0 h-12 rounded-xl"
          />
        </div>
      </div>

      <div className="px-6 mt-6">
        {/* Filter Tabs */}
        <div className="flex gap-2 mb-6 overflow-x-auto pb-2">
          {["all", "in progress", "completed"].map((filter) => (
            <Button
              key={filter}
              variant={filterStatus === filter ? "default" : "outline"}
              className={`rounded-full whitespace-nowrap ${
                filterStatus === filter
                  ? "bg-[#2D336B] hover:bg-[#1A1D3F]"
                  : "border-[#E8E9F3] text-[#2D336B] hover:bg-[#E8E9F3]"
              }`}
              onClick={() => setFilterStatus(filter)}
            >
              {filter.charAt(0).toUpperCase() + filter.slice(1)}
            </Button>
          ))}
        </div>

        {/* Orders List */}
        <div className="space-y-4">
          {filteredOrders.map((order) => (
            <Card
              key={order.id}
              className="p-4 hover:shadow-lg transition-shadow cursor-pointer border-[#E8E9F3]"
              onClick={() => onNavigate("tracking")}
            >
              <div className="flex justify-between items-start mb-3">
                <div>
                  <p className="text-sm text-gray-600 mb-1">{order.id}</p>
                  <h3 className="mb-1">{order.service}</h3>
                  <p className="text-sm text-gray-600">{order.date}</p>
                </div>
                <Badge className={order.statusColor}>{order.status}</Badge>
              </div>

              {order.status === "In Progress" && (
                <div className="mb-3">
                  <div className="flex justify-between text-sm mb-1">
                    <span className="text-gray-600">Progress</span>
                    <span className="text-[#2D336B]">{order.progress}%</span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-2">
                    <div
                      className="bg-gradient-to-r from-[#2D336B] to-[#4A5194] h-2 rounded-full"
                      style={{ width: `${order.progress}%` }}
                    ></div>
                  </div>
                </div>
              )}

              <div className="flex items-center justify-between pt-3 border-t border-gray-100">
                <div className="flex items-center gap-4">
                  <div>
                    <p className="text-xs text-gray-600 mb-1">Payment</p>
                    <div className="flex items-center gap-1">
                      {order.paymentMethod === "QRIS" ? (
                        <QrCode className="w-4 h-4 text-[#2D336B]" />
                      ) : (
                        <Banknote className="w-4 h-4 text-green-600" />
                      )}
                      <p className="text-sm">{order.paymentMethod}</p>
                    </div>
                  </div>
                  <div>
                    <p className="text-xs text-gray-600 mb-1">Total</p>
                    <p className="text-sm text-[#2D336B]">
                      Rp {order.price.toLocaleString("id-ID")}
                    </p>
                  </div>
                </div>
                <ChevronRight className="w-5 h-5 text-gray-400" />
              </div>
            </Card>
          ))}
        </div>

        {filteredOrders.length === 0 && (
          <div className="text-center py-12">
            <div className="w-20 h-20 bg-[#E8E9F3] rounded-full flex items-center justify-center mx-auto mb-4">
              <Filter className="w-10 h-10 text-[#2D336B]" />
            </div>
            <p className="text-gray-600 mb-2">No orders found</p>
            <p className="text-sm text-gray-500">Try adjusting your search or filter</p>
          </div>
        )}
      </div>
    </div>
  );
}
