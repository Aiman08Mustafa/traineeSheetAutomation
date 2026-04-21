import React, { useEffect, useMemo, useState } from 'react';
import jsPDF from 'jspdf';
import { fetchTraineeDashboardData, updateTraineeTopicStatus } from '../services/authService';

const Dashboard = ({ onLogout, user, selectedTraineeTemplateId }) => {
  const [modules, setModules] = useState([]);
  const [assignedTemplate, setAssignedTemplate] = useState(null);
  const [selectedModuleId, setSelectedModuleId] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let mounted = true;
    const load = async () => {
      try {
        const data = await fetchTraineeDashboardData({
          traineeId: user?.userId,
          token: user?.token,
          traineeTemplateId: selectedTraineeTemplateId,
        });
        if (!mounted) return;
        setAssignedTemplate(data.assignedTemplate);
        setModules(data.modules);
        if ((data.modules || []).length > 0) {
          setSelectedModuleId((prev) => prev || data.modules[0].traineeModuleId);
        } else {
          setSelectedModuleId(null);
        }
      } catch (e) {
        if (mounted) setError(e.message || 'Unable to load dashboard data.');
      } finally {
        if (mounted) setLoading(false);
      }
    };
    load();
    return () => { mounted = false; };
  }, [selectedTraineeTemplateId, user?.token, user?.userId]);

  const moduleStats = useMemo(() => modules.map((module) => {
    const completed = (module.topics || []).filter((t) => t.status === 'COMPLETED').length;
    const total = (module.topics || []).length;
    return { ...module, completed, total, progress: total ? Math.round((completed / total) * 100) : 0 };
  }), [modules]);

  const overallStats = useMemo(() => {
    const topics = moduleStats.flatMap((module) => module.topics || []);
    const total = topics.length;
    const completed = topics.filter((topic) => topic.status === 'COMPLETED').length;
    return { total, completed, progress: total ? Math.round((completed / total) * 100) : 0 };
  }, [moduleStats]);

  const selectedModule = useMemo(
    () => moduleStats.find((module) => module.traineeModuleId === selectedModuleId) || moduleStats[0] || null,
    [moduleStats, selectedModuleId]
  );

  const toggleTopic = async (moduleId, topicId, completed) => {
    try {
      const updated = await updateTraineeTopicStatus({
        traineeTopicId: topicId,
        status: completed ? 'IN_PROGRESS' : 'COMPLETED',
        token: user.token,
      });
      setModules((prev) =>
        prev.map((m) =>
          m.traineeModuleId !== moduleId
            ? m
            : { ...m, topics: m.topics.map((t) => (t.traineeTopicId === topicId ? updated : t)) }
        )
      );
    } catch (e) {
      setError(e.message || 'Unable to update topic.');
    }
  };

  const renderTextWithLinks = (text) => {
    if (!text) return 'N/A';
    const urlRegex = /(https?:\/\/[^\s]+)/g;
    const parts = String(text).split(urlRegex);
    const isUrl = (value) => /^https?:\/\/\S+$/i.test(value);
    return parts.map((part, index) => {
      if (isUrl(part)) {
        return <a key={`link-${index}`} href={part} target="_blank" rel="noopener noreferrer" className="topic-link">{part}</a>;
      }
      return <React.Fragment key={`text-${index}`}>{part}</React.Fragment>;
    });
  };

  const downloadProgressReport = () => {
    const doc = new jsPDF();
    const pageWidth = 210;
    const pageHeight = 297;
    const margin = 12;
    const maxTextWidth = pageWidth - margin * 2 - 6;
    let y = margin;

    const ensureSpace = (needed = 8) => {
      if (y + needed > pageHeight - 16) { doc.addPage(); y = margin; }
    };

    const writeWrapped = (label, value, indent = 0) => {
      const lines = doc.splitTextToSize(`${label}${value}`, maxTextWidth - indent);
      lines.forEach((line) => { ensureSpace(6); doc.text(line, margin + indent, y); y += 5.5; });
    };

    const traineeName = user?.name || assignedTemplate?.traineeName || 'N/A';
    doc.setFillColor(221, 234, 255);
    doc.rect(0, 0, pageWidth, 28, 'F');
    doc.setTextColor(35, 41, 53);
    doc.setFontSize(18);
    doc.setFont('helvetica', 'bold');
    doc.text('Trainee Progress Report', margin, 16);
    doc.setFontSize(10);
    doc.setFont('helvetica', 'normal');
    doc.text(new Date().toLocaleString(), margin, 23);
    y = 36;

    doc.setDrawColor(196, 217, 245);
    doc.setFillColor(242, 248, 255);
    doc.roundedRect(margin, y, pageWidth - margin * 2, 28, 2, 2, 'FD');
    doc.setTextColor(35, 41, 53);
    doc.setFontSize(11);
    doc.setFont('helvetica', 'bold');
    doc.text(`Trainee: ${traineeName}`, margin + 4, y + 8);
    doc.text(`Template: ${assignedTemplate?.templateTitle || 'Not assigned'}`, margin + 4, y + 14);
    doc.text(`Overall Completion: ${overallStats.progress}% (${overallStats.completed}/${overallStats.total} topics)`, margin + 4, y + 20);
    y += 36;

    moduleStats.forEach((module, moduleIndex) => {
      ensureSpace(14);
      doc.setFillColor(232, 242, 255);
      doc.roundedRect(margin, y, pageWidth - margin * 2, 9, 1.5, 1.5, 'F');
      doc.setFontSize(11);
      doc.setFont('helvetica', 'bold');
      doc.setTextColor(33, 87, 125);
      doc.text(`${moduleIndex + 1}. ${module.moduleName}  (${module.progress}%)`, margin + 3, y + 6.2);
      y += 13;

      (module.topics || []).slice().sort((a, b) => (a.sequenceOrder || 0) - (b.sequenceOrder || 0)).forEach((topic, index) => {
        const seq = topic.sequenceOrder || index + 1;
        const objective = topic.learningObjective || topic.learningObjectives || 'N/A';
        const reading = topic.readingMaterial || topic.readingMaterials || 'N/A';
        const status = topic.status || 'NOT_STARTED';
        ensureSpace(22);
        doc.setFontSize(10);
        doc.setFont('helvetica', 'bold');
        doc.setTextColor(25, 30, 40);
        writeWrapped(`${seq}. ${topic.topicName}  [Status: `, `${status}]`, 2);
        doc.setFont('helvetica', 'normal');
        doc.setTextColor(65, 74, 89);
        writeWrapped('Objective: ', objective, 6);
        writeWrapped('Reading: ', reading, 6);
        writeWrapped('Assignment: ', topic.assignment || 'N/A', 6);
        y += 1;
      });
      y += 2;
    });

    const pages = doc.getNumberOfPages();
    for (let i = 1; i <= pages; i++) {
      doc.setPage(i);
      doc.setFontSize(9);
      doc.setTextColor(120, 126, 135);
      doc.text(`Page ${i} of ${pages}`, pageWidth - 34, pageHeight - 8);
    }
    doc.save(`trainee-progress-report-${user?.userId || 'user'}.pdf`);
  };

  const traineeName = user?.name || user?.firstName || 'Trainee';
  const circumference = 2 * Math.PI * 40;

  return (
    <div className="nd-shell">
      {/* TOP NAVBAR */}
      <nav className="nd-navbar">
        <div className="nd-navbar-logo">
          <div className="nd-logo-icon">
            <svg viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg" width="28" height="28">
              <rect width="32" height="32" rx="8" fill="#7C6BF8"/>
              <path d="M8 22L13 10L18 18L21 14L24 22H8Z" fill="white" opacity="0.9"/>
              <circle cx="21" cy="11" r="3" fill="#C4B8FF"/>
            </svg>
          </div>
          <span className="nd-logo-text">Contour Software</span>
        </div>
        <div className="nd-navbar-right">
          <button className="nd-logout-btn" onClick={onLogout}>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
              <polyline points="16 17 21 12 16 7"/>
              <line x1="21" y1="12" x2="9" y2="12"/>
            </svg>
            Log Out
          </button>
        </div>
      </nav>

      <div className="nd-body">
        {/* LEFT PANEL */}
        <aside className="nd-left-panel">
          <div className="nd-user-greeting">
            <h2 className="nd-greeting-name">{traineeName}'s Dashboard</h2>
            <p className="nd-greeting-sub">{assignedTemplate?.templateTitle || 'No template assigned'}</p>
          </div>

          {/* Progress Chart */}
          <div className="nd-progress-card">
            <div className="nd-progress-header">
              <span className="nd-progress-label">Overall Progress</span>
              <button className="nd-pdf-btn" onClick={downloadProgressReport} title="Download PDF Report">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                  <polyline points="7 10 12 15 17 10"/>
                  <line x1="12" y1="15" x2="12" y2="3"/>
                </svg>
              </button>
            </div>
            <div className="nd-donut-wrap">
              <svg viewBox="0 0 100 100" className="nd-donut-svg">
                <circle cx="50" cy="50" r="40" className="nd-donut-bg"/>
                <circle
                  cx="50" cy="50" r="40"
                  className="nd-donut-value"
                  style={{
                    strokeDasharray: circumference,
                    strokeDashoffset: circumference - (circumference * overallStats.progress) / 100,
                  }}
                />
              </svg>
              <div className="nd-donut-center">
                <span className="nd-donut-pct">{overallStats.progress}%</span>
                <span className="nd-donut-done">{overallStats.completed}/{overallStats.total}</span>
              </div>
            </div>
          </div>

          {/* Modules List */}
          <div className="nd-modules-section">
            <h3 className="nd-modules-title">Modules</h3>
            <ul className="nd-module-list">
              {moduleStats.map((module, i) => (
                <li key={module.traineeModuleId}>
                  <button
                    className={`nd-module-btn ${selectedModule?.traineeModuleId === module.traineeModuleId ? 'active' : ''}`}
                    onClick={() => setSelectedModuleId(module.traineeModuleId)}
                  >
                    <div className="nd-module-btn-left">
                      <span className="nd-module-num">{i + 1}</span>
                      <span className="nd-module-name">{module.moduleName}</span>
                    </div>
                    <div className="nd-module-pill">{module.progress}%</div>
                  </button>
                </li>
              ))}
            </ul>
          </div>
        </aside>

        {/* RIGHT PANEL */}
        <main className="nd-right-panel">
          {loading && <p className="nd-loading">Loading...</p>}
          {error && <p className="nd-error">{error}</p>}

          {!loading && !error && (
            <>
              <div className="nd-content-header">
                <h2 className="nd-content-title">
                  {selectedModule ? selectedModule.moduleName : 'Select a Module'}
                </h2>
                {selectedModule && (
                  <span className="nd-content-meta">
                    {selectedModule.completed} of {selectedModule.total} topics completed
                  </span>
                )}
              </div>

              {!selectedModule ? (
                <p className="nd-empty">No modules available yet.</p>
              ) : (
                <div className="nd-table-wrap">
                  <table className="nd-topics-table">
                    <thead>
                      <tr>
                        <th>#</th>
                        <th>Topic</th>
                        <th>Learning Objective</th>
                        <th>Reading Material</th>
                        <th>Assignment</th>
                        <th>Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      {(selectedModule.topics || [])
                        .slice()
                        .sort((a, b) => (a.sequenceOrder || 0) - (b.sequenceOrder || 0))
                        .map((topic, index) => {
                          const done = topic.status === 'COMPLETED';
                          const seq = topic.sequenceOrder || index + 1;
                          const objective = topic.learningObjective || topic.learningObjectives || '—';
                          const reading = topic.readingMaterial || topic.readingMaterials || '—';
                          return (
                            <tr key={topic.traineeTopicId} className={done ? 'row-done' : ''}>
                              <td className="td-seq">{seq}</td>
                              <td className="td-name">{topic.topicName}</td>
                              <td className="td-obj">{objective}</td>
                              <td className="td-read">{renderTextWithLinks(reading)}</td>
                              <td className="td-assign">{topic.assignment || '—'}</td>
                              <td className="td-status">
                                <label className="nd-toggle">
                                  <input
                                    type="checkbox"
                                    checked={done}
                                    onChange={() => toggleTopic(selectedModule.traineeModuleId, topic.traineeTopicId, done)}
                                  />
                                  <span className={`nd-badge ${done ? 'badge-done' : 'badge-pending'}`}>
                                    {done ? 'Done' : 'Pending'}
                                  </span>
                                </label>
                              </td>
                            </tr>
                          );
                        })}
                    </tbody>
                  </table>
                </div>
              )}
            </>
          )}
        </main>
      </div>
    </div>
  );
};

export default Dashboard;