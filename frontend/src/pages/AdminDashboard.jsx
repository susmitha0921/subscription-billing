import { useState, useEffect } from 'react';
import axios from 'axios';

export default function AdminDashboard() {
  const [plans, setPlans] = useState([]);
  const [subscriptions, setSubscriptions] = useState([]);
  const [invoices, setInvoices] = useState([]);
  const [newPlan, setNewPlan] = useState({ name: '', description: '', billingCycle: 'MONTHLY', price: '', features: '' });

  const fetchData = async () => {
    try {
      const [planRes, subRes, invRes] = await Promise.all([
        axios.get('/api/plans'),
        axios.get('/api/subscriptions'),
        axios.get('/api/invoices')
      ]);
      setPlans(planRes.data);
      setSubscriptions(subRes.data);
      setInvoices(invRes.data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleCreatePlan = async (e) => {
    e.preventDefault();
    try {
      await axios.post('/api/plans', newPlan);
      setNewPlan({ name: '', description: '', billingCycle: 'MONTHLY', price: '', features: '' });
      fetchData();
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div>
      <h1>Admin Dashboard</h1>

      <div className="card">
        <h2>Create Subscription Plan</h2>
        <form onSubmit={handleCreatePlan}>
          <input placeholder="Name" value={newPlan.name} onChange={e => setNewPlan({...newPlan, name: e.target.value})} required />
          <input placeholder="Description" value={newPlan.description} onChange={e => setNewPlan({...newPlan, description: e.target.value})} />
          <select value={newPlan.billingCycle} onChange={e => setNewPlan({...newPlan, billingCycle: e.target.value})}>
            <option value="MONTHLY">Monthly</option>
            <option value="YEARLY">Yearly</option>
          </select>
          <input type="number" placeholder="Price" value={newPlan.price} onChange={e => setNewPlan({...newPlan, price: e.target.value})} required />
          <input placeholder="Features (comma separated)" value={newPlan.features} onChange={e => setNewPlan({...newPlan, features: e.target.value})} />
          <button type="submit">Create Plan</button>
        </form>
      </div>

      <div className="card">
        <h2>Available Plans</h2>
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Cycle</th>
              <th>Price</th>
              <th>Active</th>
            </tr>
          </thead>
          <tbody>
            {plans.map(p => (
              <tr key={p.id}>
                <td>{p.id}</td>
                <td>{p.name}</td>
                <td>{p.billingCycle}</td>
                <td>${p.price}</td>
                <td>{p.isActive ? 'Yes' : 'No'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="card">
        <h2>All Subscriptions</h2>
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>User ID</th>
              <th>Plan</th>
              <th>Status</th>
              <th>Next Billing Date</th>
            </tr>
          </thead>
          <tbody>
            {subscriptions.map(s => (
              <tr key={s.id}>
                <td>{s.id}</td>
                <td>{s.user.id}</td>
                <td>{s.plan.name}</td>
                <td>{s.status}</td>
                <td>{new Date(s.nextBillingDate).toLocaleDateString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="card">
        <h2>All Invoices</h2>
        <table>
          <thead>
            <tr>
              <th>Invoice Number</th>
              <th>Subscription ID</th>
              <th>Amount</th>
              <th>Status</th>
              <th>Due Date</th>
            </tr>
          </thead>
          <tbody>
            {invoices.map(i => (
              <tr key={i.id}>
                <td>{i.invoiceNumber}</td>
                <td>{i.subscription.id}</td>
                <td>${i.amount}</td>
                <td>{i.status}</td>
                <td>{new Date(i.dueDate).toLocaleDateString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

    </div>
  );
}
